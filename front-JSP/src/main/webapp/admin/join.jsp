<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <title>회원가입</title>
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-8">
            <h4 class="mb-4 pb-2 border-bottom fw-bold">회원 등록</h4>
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <th class="table-secondary w-25 align-middle">
                        아이디 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <div class="d-flex gap-2">
                            <input type="text" class="form-control" id="loginId" placeholder="아이디 입력"/>
                            <button class="btn btn-secondary text-nowrap" onclick="checkId()">중복확인</button>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">
                        비밀번호 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <input type="password" class="form-control" id="password" placeholder="비밀번호 입력"/>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">
                        이름 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <input type="text" class="form-control" id="name" placeholder="이름 입력"/>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">
                        생년월일 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <input type="text" class="form-control" id="birthDate" placeholder="YYYY-MM-DD"/>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">
                        성별 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <select class="form-select" id="gender">
                            <option value="">선택</option>
                            <option value="MALE">남성</option>
                            <option value="FEMALE">여성</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">
                        휴대폰번호 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <input type="text" class="form-control" id="phone" placeholder="휴대폰번호 입력 (예: 01012341234)"/>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">
                        주소 <span class="text-danger">*</span>
                    </th>
                    <td>
                        <input type="text" class="form-control" id="address" placeholder="주소 입력"/>
                    </td>
                </tr>
                <tr>
                    <th class="table-secondary align-middle">이메일</th>
                    <td>
                        <input type="email" class="form-control" id="email" placeholder="이메일 입력 (예: example@email.com) (선택)"/>
                    </td>
                </tr>
                </tbody>
            </table>
            <div class="d-flex justify-content-center gap-2 mt-3">
                <button class="btn btn-outline-secondary px-4"
                        onclick="location.href='admin.jsp'">취소</button>
                <button class="btn btn-dark px-4" onclick="join()">등록</button>
            </div>
        </div>
    </div>
</div>

<script src="js/config.js"></script>
<script src="js/member/member.js"></script>
</body>
</html>