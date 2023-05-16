package com.example.ec.models;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.example.ec.services.EntityRevisionListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@RevisionEntity(value = EntityRevisionListener.class)
@Table(name = "REVINFO")
@Getter
@Setter
@NoArgsConstructor
public class Revision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "rev")
    private Long revisionId;

    @RevisionTimestamp
    @Column(name = "revision_date")
    private Date revisionDate;

    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID user;
}
