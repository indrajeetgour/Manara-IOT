package com.slb.manara;

import java.util.Objects;

public class WellAndStationMapping {

    private String  nameCol;
    private Integer idCol;
    private String  wellOrTimeStamp;

    public WellAndStationMapping(String nameCol, Integer idCol, String wellOrTimeStamp) {
        this.nameCol = nameCol;
        this.idCol = idCol;
        this.wellOrTimeStamp = wellOrTimeStamp;
    }

    @Override
    public String toString() {
        return "WellAndStationMapping{" +
                "nameCol='" + nameCol + '\'' +
                ", idCol=" + idCol +
                ", wellOrTimeStamp='" + wellOrTimeStamp + '\'' +
                '}';
    }

    public String getNameCol() {
        return nameCol;
    }

    public void setNameCol(String nameCol) {
        this.nameCol = nameCol;
    }

    public Integer getIdCol() {
        return idCol;
    }

    public void setIdCol(Integer idCol) {
        this.idCol = idCol;
    }

    public String getWellOrTimeStamp() {
        return wellOrTimeStamp;
    }

    public void setWellOrTimeStamp(String wellOrTimeStamp) {
        this.wellOrTimeStamp = wellOrTimeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WellAndStationMapping that = (WellAndStationMapping) o;
        return Objects.equals(nameCol, that.nameCol) &&
                Objects.equals(idCol, that.idCol);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nameCol, idCol);
    }
}
