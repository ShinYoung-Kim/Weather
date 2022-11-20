package com.ksy.mpl;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Statistics {
    public int temperature;
    @PrimaryKey
    public Cloth cloth;
    public int wearCount;
}
