package model;

/**
 * Based on a solution by Saimir Bala 
 * Source repository: https://github.com/s41m1r/MiningVCS/tree/VisualizationBranch/MiningSVN
 * 
 * Altered the class Change in order to make it compatible with EnrichedChanges
 */
public class Change extends ChangeTemplate{
	
	
	/**
	 * @param action
	 * @param path
	 */
   public Change(String action, String path) {
	   super(action, path);
   }

	@Override
   public String toString() {
	   return "Change [action=" + getAction() + ", path=" + getPath() + "]";
   }
	
}
