package project;

public class Main {

    public static void main(String[] args) {
        loadNatives();

        //Window window = new Window();
        //window.showImage(mat);
    }

    private static void loadNatives() {
        Natives.load(Natives.OPENCV_LIBNAME);
    }
}
