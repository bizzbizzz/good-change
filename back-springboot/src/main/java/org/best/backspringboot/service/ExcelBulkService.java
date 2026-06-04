package org.best.backspringboot.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.best.backspringboot.dto.card.CardCreateDto;
import org.best.backspringboot.dto.excel.ExcelUploadResultDto;
import org.best.backspringboot.dto.member.MemberCreateDto;
import org.best.backspringboot.dto.member.MemberRegisterDto;
import org.best.backspringboot.dto.merchant.MerchantCreateDto;
import org.best.backspringboot.dto.merchant.MerchantRegisterDto;
import org.best.backspringboot.entity.Member;
import org.best.backspringboot.entity.Merchant;
import org.best.backspringboot.exception.BulkUploadException;
import org.best.backspringboot.mapper.CardMapper;
import org.best.backspringboot.mapper.MemberMapper;
import org.best.backspringboot.mapper.MerchantMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelBulkService {

    private final MemberService          memberService;
    private final MerchantService        merchantService;

    // ============================================================
    //  수혜자 일괄 등록
    //  컬럼: 이름 | 생년월일 | 소속 | 성별 | 주소 | 이메일 | 포인트 | 카드번호
    //  아이디 = 카드번호 뒷 4자리,  비번 = 카드번호 뒷4자리 + "!"
    // ============================================================
    @Transactional
    public ExcelUploadResultDto bulkInsertMembers(MultipartFile file) {
        List<String> errors  = new ArrayList<>();
        int success = 0;
        int total   = 0;

        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)) {

            Sheet sheet = wb.getSheetAt(0);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (isEmptyRow(row)) continue;
                total++;

                // 검증/파싱 (행 단위 에러 수집)
                String name         = getString(row, 0);
                String birthDateStr = getString(row, 1);
                String organization = getString(row, 2);
                String genderStr    = getString(row, 3);
                String address      = getString(row, 4);
                String email        = getString(row, 5);
                long   point        = getLong(row, 6);
                String cardNumber   = getString(row, 7);

                try {
                    if (name.isEmpty()) throw new IllegalArgumentException("이름 누락");
                    if (cardNumber.isEmpty() || cardNumber.length() != 16)
                        throw new IllegalArgumentException("카드번호 16자리 오류");

                    String last4   = cardNumber.substring(12);
                    String loginId = last4;
                    String rawPw   = last4 + "!";

                    LocalDate birthDate;
                    try {
                        birthDate = LocalDate.parse(birthDateStr);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("생년월일 형식 오류 (YYYY-MM-DD)");
                    }

                    String gender;
                    if (genderStr.equalsIgnoreCase("M") || genderStr.equals("남")) {
                        gender = "MALE";
                    } else if (genderStr.equalsIgnoreCase("F") || genderStr.equals("여")) {
                        gender = "FEMALE";
                    } else {
                        throw new IllegalArgumentException("성별 오류 (M/F)");
                    }

                    MemberCreateDto member = MemberCreateDto.builder()
                            .loginId(loginId)
                            .password(rawPw)
                            .name(name)
                            .birthDate(birthDate)
                            .gender(gender)
                            .address(address)
                            .email(email)
                            .point(point)
                            .organization(organization)
                            .roleId(2L)
                            .build();

                    CardCreateDto card = CardCreateDto.builder()
                            .cardNumber(cardNumber)
                            .build();

                    MemberRegisterDto registerDto = new MemberRegisterDto();
                    registerDto.setMember(member);
                    registerDto.setCards(List.of(card));

                    memberService.create(registerDto);
                    success++;

                } catch (Exception e) {
                    errors.add((r + 1) + "행: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            errors.add("파일 처리 오류: " + e.getMessage());
        }

        // ── 하나라도 실패하면 전체 롤백 ──
        if (!errors.isEmpty()) {
            throw new BulkUploadException(
                    ExcelUploadResultDto.builder()
                            .totalCount(total)
                            .successCount(0)          // 전체 롤백되므로 0
                            .failCount(errors.size())
                            .errors(errors)
                            .build()
            );
        }

        return ExcelUploadResultDto.builder()
                .totalCount(total)
                .successCount(success)
                .failCount(0)
                .errors(errors)
                .build();
    }


    // ============================================================
    //  가맹점 일괄 등록
    //  컬럼: 가맹점명 | 사업자번호 | 대표자명 | 연락처 | 주소 | 이메일 | 업종(categoryId)
    //  아이디 = 사업자번호,  비번 = 사업자번호 + "!"
    // ============================================================
    @Transactional
    public ExcelUploadResultDto bulkInsertMerchants(MultipartFile file) {
        List<String> errors  = new ArrayList<>();
        int success = 0;
        int total   = 0;

        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)) {

            Sheet sheet = wb.getSheetAt(0);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (isEmptyRow(row)) continue;
                total++;

                try {
                    String merchantName   = getString(row, 0);
                    String businessNo     = getString(row, 1);
                    String representative = getString(row, 2);
                    String contact = getString(row, 3).replaceAll("[^0-9]", "");  // 숫자만 남김
                    String address        = getString(row, 4);
                    String email          = getString(row, 5);
                    long   categoryId     = getLong(row, 6);
                    String ipAddress      = getString(row, 7);

                    // 필수 검증
                    if (merchantName.isEmpty())   throw new IllegalArgumentException("가맹점명 누락");
                    if (businessNo.isEmpty())     throw new IllegalArgumentException("사업자번호 누락");
                    if (representative.isEmpty()) throw new IllegalArgumentException("대표자명 누락");
                    if (categoryId <= 0)          throw new IllegalArgumentException("업종ID 누락");
                    if (ipAddress.isEmpty())      throw new IllegalArgumentException("IP주소 누락");


                    // 아이디 = 사업자번호, 비번 = 사업자번호 + "!"
                    String loginId = businessNo;
                    String rawPw   = businessNo + "!";

                    // ── MemberCreateDto 구성 (가맹점 계정) ──
                    MemberCreateDto memberDto = MemberCreateDto.builder()
                            .loginId(loginId)
                            .password(rawPw)
                            .name(merchantName)       // 가맹점명을 이름으로
                            .birthDate(LocalDate.of(1990, 1, 1))  // 더미값 추가
                            .gender("MALE")                        // 더미값 추가
                            .address(address)
                            .email(email)
                            .point(0L)                // 더미값 추가
                            .roleId(3L)               // MERCHANT
                            .build();

                    MemberRegisterDto memberRegisterDto = new MemberRegisterDto();
                    memberRegisterDto.setMember(memberDto);
                    memberRegisterDto.setCards(List.of());  // 가맹점은 카드 없음

                    // ── MerchantCreateDto 구성 ──
                    MerchantCreateDto merchantDto = MerchantCreateDto.builder()
                            .merchantName(merchantName)
                            .businessNumber(businessNo)
                            .representative(representative)
                            .contact(contact)
                            .address(address)
                            .email(email)
                            .categoryId(categoryId)
                            .build();

                    // ── MerchantRegisterDto 구성 후 기존 서비스 호출 ──
                    MerchantRegisterDto registerDto = new MerchantRegisterDto();
                    registerDto.setMember(memberRegisterDto);
                    registerDto.setMerchant(merchantDto);
                    registerDto.setIpAddress(ipAddress);

                    merchantService.createWithMember(registerDto);

                    success++;

                } catch (Exception e) {
                    errors.add((r + 1) + "행: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            errors.add("파일 처리 오류: " + e.getMessage());
        }

        if (!errors.isEmpty()) {
            throw new BulkUploadException(
                    ExcelUploadResultDto.builder()
                            .totalCount(total)
                            .successCount(0)
                            .failCount(errors.size())
                            .errors(errors)
                            .build()
            );
        }

        return ExcelUploadResultDto.builder()
                .totalCount(total)
                .successCount(success)
                .failCount(0)
                .errors(errors)
                .build();
    }


    // ============================================================
    //  템플릿 생성
    // ============================================================
    public byte[] createMemberTemplate() throws Exception {
        String[] headers = {"이름", "생년월일(YYYY-MM-DD)", "소속", "성별(M/F)",
                "주소", "이메일", "포인트", "카드번호(16자리)"};
        String[] example = {"홍길동", "1990-01-01", "OO센터", "M",
                "서울시 강남구", "hong@test.com", "100000", "1234567812345678"};
        return createTemplate(headers, example);
    }

    public byte[] createMerchantTemplate() throws Exception {
        String[] headers = {"가맹점명", "사업자번호", "대표자명", "연락처",
                "주소", "이메일", "업종ID(1:식당 2:문구점 3:병의원)", "IP주소"};
        String[] example = {"OO식당", "1234567890", "김대표", "02-123-4567",
                "서울시 종로구", "store@test.com", "1", "192.168.0.1"};
        return createTemplate(headers, example);
    }

    private byte[] createTemplate(String[] headers, String[] example) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("일괄등록");

            // 헤더 스타일
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 행
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            // 예시 행
            Row exampleRow = sheet.createRow(1);
            for (int i = 0; i < example.length; i++) {
                exampleRow.createCell(i).setCellValue(example[i]);
            }

            wb.write(bos);
            return bos.toByteArray();
        }
    }


    // ============================================================
    //  엑셀 셀 헬퍼
    // ============================================================
    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String v = getCellString(cell);
                if (v != null && !v.trim().isEmpty()) return false;
            }
        }
        return true;
    }

    private String getString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return getCellString(cell).trim();
    }

    private long getLong(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return 0;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (long) cell.getNumericCellValue();
            }
            return Long.parseLong(getCellString(cell).trim().replaceAll(",", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private String getCellString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(cell.getDateCellValue());
                }
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d) && !Double.isInfinite(d)) {
                    return String.format("%.0f", d);
                }
                return String.valueOf(d);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default:      return "";
        }
    }
}
