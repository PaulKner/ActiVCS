package model;

import java.io.Serializable;

/**
 * Based on a solution by Saimir Bala 
 * Source repository: https://github.com/s41m1r/MiningVCS/tree/VisualizationBranch/MiningSVN
 * 
 * Altered the class Change in order to make it compatible with EnrichedChanges
 */
public abstract class ChangeTemplate implements Serializable{
	private String action;
	private String path;
	
	public ChangeTemplate(String action, String path) {
		this.setAction(action);
		this.setPath(path);
	}
	
	public abstract String toString();

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
