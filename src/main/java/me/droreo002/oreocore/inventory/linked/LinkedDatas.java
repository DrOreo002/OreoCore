package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LinkedDatas {

    @Getter
    private final List<LinkedData> data;

    public LinkedDatas() {
        this.data = new ArrayList<>();
    }

    /**
     * Filter data by DataType
     *
     * @param dataType The data type
     * @return the filtered data
     */
    public List<LinkedData> filter(LinkedDataType dataType) {
        return data.stream().filter(dat -> dat.getDataType() == dataType).collect(Collectors.toList());
    }

    /**
     * Get LinkedData by data key (string)
     *
     * @param dataKey The data key
     * @return the LinkedData if available, null otherwise
     */
    public LinkedData getData(String dataKey) {
        return data.stream().filter(dat -> dat.getDataKey().equals(dataKey)).findAny().orElse(null);
    }

    /**
     * Check if data contains that key
     *
     * @param dataKey The data key
     * @return true if contains, false otherwise
     */
    public boolean contains(String dataKey) {
        return getData(dataKey) != null;
    }

    /**
     * Remove the data from the list
     *
     * @param dataKey The data key
     */
    public void removeData(String dataKey) {
        data.removeIf(dat -> dat.getDataKey().equals(dataKey));
    }

    /**
     * Add all data
     *
     * @param datas The data to add
     */
    public void addAll(List<LinkedData> datas) {
        data.addAll(datas);
    }

    /**
     * Add data into the list
     *
     * @param linkedData The linked data
     */
    public void addData(LinkedData linkedData) {
        data.add(linkedData);
    }
}
