package model;

/**
 * @author Paul Kneringer
 * New form of Change element that includes activitytype labels
 */
public class EnrichedChange extends ChangeTemplate{
	private String activityLabel = "unknown";
	
	public EnrichedChange(String action, String path) {
		super(action, path);
	}
	
	public EnrichedChange(String action, String path, String activityLabel) {
		super(action, path);
		this.activityLabel = activityLabel;
	}
	
	public String getActivityLabel() {
		return activityLabel;
	}
	public void setActivityLabel(String activityLabel) {
		this.activityLabel = activityLabel;
	}

	@Override
	public String toString() {
		return "Change [action=" + getAction() + ", path=" + getPath() + ", activitylabel= "+ this.activityLabel+"]";
	}

}
