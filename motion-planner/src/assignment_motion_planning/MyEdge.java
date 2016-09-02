package assignment_motion_planning;

/**
 * Created by vivek on 1/26/16.
 */
public class MyEdge {
    private Vector parentConfig;
    private Vector control;
    private double duration;
    private Vector childConfig;

    public MyEdge(Vector childConfig,Vector parentConfig, Vector control, double duration){
        this.childConfig = childConfig;
        this.parentConfig = parentConfig;
        this.control = control;
        this.duration = duration;
    }

    public Vector getParent(){
        return this.parentConfig;
    }

    public Vector getControl() {
        return control;
    }

    public double getDuration() {
        return duration;
    }

    public Vector getChildConfig() {
        return childConfig;
    }
}
