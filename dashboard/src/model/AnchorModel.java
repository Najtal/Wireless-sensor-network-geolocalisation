package model;

import ucc.AnchorDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jvdur on 19/05/2016.
 */
public class AnchorModel {

    public static final AnchorModel INSTANCE = new AnchorModel();
    private List<AnchorDTO> anchors;
    private Map<Integer, AnchorDTO> anchorIdMap;

    /**
     * Constructor for AnchorModel
     */
    private AnchorModel() {
        this.anchors = new ArrayList<>();
        this.anchorIdMap = new HashMap<>();
    }


    public void addAnchor(AnchorDTO anchor) {
        if (!anchors.contains(anchor) && !anchorIdMap.containsKey(anchor.getId())) {
            anchors.add(anchor);
            anchorIdMap.put(anchor.getId(), anchor);
        }
    }

    public void removeAnchor(AnchorDTO anchor) {
        if (!anchors.contains(anchor) && !anchorIdMap.containsKey(anchor.getId())) {
            anchors.remove(anchor);
            anchorIdMap.remove(anchor.getId());
        }
    }

    public AnchorDTO getAnchorById(int id) {
        return anchorIdMap.get(id);
    }

    public List<AnchorDTO> getAnchorBy() {
        return anchors;
    }

}
