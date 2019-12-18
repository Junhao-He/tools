package sam.znv.lopq;


public class CoarseClassify {
    private static final FeatureCompUtil fc = new FeatureCompUtil();

    public static String getClassify(byte[] feature) {
        if (feature == null) {
            return "-1";
        }
        float[] floatArray = fc.getFloatArray(feature);
        int[] coarseCode = LOPQModel.predict(floatArray);
        return String.valueOf(coarseCode[0]);
    }
}
