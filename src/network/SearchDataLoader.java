package network;

public class SearchDataLoader {

	private OnSearchListener listener;
	private String newString, oldString;
	private int minLengthToSearch;

	public SearchDataLoader(int minCharToSearch) {
		newString = "";
		oldString = "";
		this.minLengthToSearch = minCharToSearch;

	}

	public interface OnSearchListener {
		/**
		 * Invalidating the view after search the string. Must call main UI for
		 * this method;
		 * 
		 * @param data
		 *            - JSONObject
		 */
		public void onSearchComplete(String data);

		/**
		 * Setup the function to search the input.
		 * 
		 * @param newString
		 * @return JSONObject of the searched newString
		 */
		public String runSearchFunction(String newString);

		/**
		 * Must call main UI for this method;
		 */
		public void defaultUI();
	}

	public void setOnSearchListener(OnSearchListener listener) {
		this.listener = listener;

	}

	SearchThread searchThread;

	public void searchNewString(String newString) {

			this.newString = newString;
		
		if (searchThread == null)
			searchThread = new SearchThread();

		if (!searchThread.isAlive()) {
			searchThread = new SearchThread();
		}
		if (searchThread.getState() == Thread.State.NEW)
			searchThread.start();
	}

	class SearchThread extends Thread {
		@Override
		public void run() {
			while (newString.compareTo(oldString) != 0) {

				oldString = newString;

				if (newString.length() >= minLengthToSearch) {
					oldString = newString;
					String data = listener.runSearchFunction(newString);
					// There might be a new string coming in;
					if (newString.compareTo(oldString) == 0) {
						// Update the view
						listener.onSearchComplete(data);

					}
				} else {
					listener.defaultUI();
				}

			}
		}
	}
}
