package org.best.backspringboot.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonFile {
    private Long fileId;
    private String refType;
    private Long refId;
    private String fileName;
    private String storedName;
    private String filePath;
    private String fileExt;
    private Long fileSize;
    private String mimeType;
    private Integer sortOrder;
    private Integer isActive;
    private LocalDateTime createdAt;
}
