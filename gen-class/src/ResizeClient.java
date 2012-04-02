public class ResizeClient {
    public static void main (String[] args) {
        ResizeImage.resizeFile(args[0], args[1], Double.parseDouble(args[2]));
    }
}