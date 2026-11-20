package br.com.nexoimob.entities;

import jakarta.persistence.*;
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ids")
    @SequenceGenerator(name = "ids", sequenceName = "seq_ids", allocationSize = 1)
    private Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
