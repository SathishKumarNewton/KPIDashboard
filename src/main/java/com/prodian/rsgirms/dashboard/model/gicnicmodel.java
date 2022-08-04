package com.prodian.rsgirms.dashboard.model;

import javax.persistence.*;

import lombok.Data;


@Data
@Entity
@Table(name = "players")
public class gicnicmodel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "player_id")
    private Integer playerId;
    

}

