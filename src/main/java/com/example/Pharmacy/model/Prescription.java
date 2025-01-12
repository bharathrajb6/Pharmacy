package com.example.Pharmacy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescription")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prescription {

    @Id
    @Column(name = "prescription_id")
    private String prescriptionId;

    @Column(name = "username")
    private String username;

    @Lob
    @Column(name = "uploaded_file")
    private byte[] uploadedFile;

    @Column(name = "status")
    private String status;

    @Column(name = "comments")
    private String comments;

    @Column(name = "file_type")
    private String fileType;
}
