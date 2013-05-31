package org.jumpmind.symmetric.io.data.transform;

import java.util.Map;

import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.extension.IBuiltInExtensionPoint;
import org.jumpmind.symmetric.io.data.DataContext;

public class RemoveColumnTransform implements ISingleValueColumnTransform, IBuiltInExtensionPoint {

    public final static String NAME = "remove";

    public String getName() {
        return NAME;
    }
        
    public boolean isExtractColumnTransform() {
        return true;
    }
    
    public boolean isLoadColumnTransform() {
        return true;
    }

    public String transform(IDatabasePlatform platform, DataContext context,
            TransformColumn column, TransformedData data, Map<String, String> sourceValues, String newValue, String oldValue)  
                    throws IgnoreColumnException, IgnoreRowException {
        throw new IgnoreColumnException();
    }

}
