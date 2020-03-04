package eu.indiewalkabout.fridgemanager.data;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "FOODLIST")
public class FoodEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    @ColumnInfo(name = "EXPIRING_AT")
    @TypeConverters({DateConverter.class})
    private Date expiringAt;

    private int done;

    /**
     * ---------------------------------------------------------------------------------------------
     * Create a new FoodEntry
     * ---------------------------------------------------------------------------------------------
     * @param done
     * @param name
     * @param expiringAt
     */
    @Ignore
    public FoodEntry(int done, String name, Date expiringAt) {
        this.done       = done;
        this.name       = name;
        this.expiringAt = expiringAt;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Create a new FoodEntry for db
     * ---------------------------------------------------------------------------------------------
     * @param id
     * @param done
     * @param name
     * @param expiringAt
     */
    public FoodEntry(int id, int done, String name, Date expiringAt) {
        this.id         = id;
        this.done       = done;
        this.name       = name;
        this.expiringAt = expiringAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiringAt() {
        return expiringAt;
    }

    public void setExpiringAt(Date expiringAt) {
        this.expiringAt = expiringAt;
    }
}
