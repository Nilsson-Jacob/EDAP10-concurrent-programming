import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    private final JProgressBar mainProgressBar;
    private ExecutorService threadPool;
    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();
        threadPool 		= Executors.newFixedThreadPool(2);
        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        w.enableErrorChecks();
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        	System.out.println("message intercepted (N=" + n + ")...");
        	SwingUtilities.invokeLater(() -> {
        		WorklistItem workItem = new WorklistItem(n,message);
        		workList.add(workItem);
        		JButton breakButton = new JButton("Break");
        		breakButton.addActionListener(e -> {
        			onBreakButton(message, n);
        			workList.remove(workItem);
        		});	
        		workItem.add(breakButton);
        });	
    }
    
    private void onBreakButton(String message, BigInteger n) {
		ProgressItem progressItem = new ProgressItem(n,message);
		progressList.add(progressItem);
		mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
		ProgressTracker tracker = new Tracker(progressItem, mainProgressBar);
		JButton cancelButton = new JButton("Cancel");
		progressItem.add(cancelButton);
		
		Future<?> task = threadPool.submit(() -> {
			try {
			String result =	Factorizer.crack(message, n, tracker);
			
			SwingUtilities.invokeLater(() -> {
				JTextArea text = progressItem.getTextArea();
				text.setText(result);
				progressItem.remove(cancelButton);
				createRemoveButton(progressItem);
			});
			
			} catch (InterruptedException d) {
				// TODO Auto-generated catch block
				d.printStackTrace();
			}
		});
		
		
		cancelButton.addActionListener(z -> {
			onCancelButton(progressItem, cancelButton, task,tracker);
		});
	}

	private void onCancelButton(ProgressItem progressItem, JButton cancelButton, Future<?> task, ProgressTracker tracker) {
		task.cancel(true);
		int barValue = progressItem.getProgressBar().getValue();
		progressItem.getTextArea().setText("[cancelled]");
		tracker.setTotalProgress(1000000);
		progressItem.getProgressBar().setValue(1000000);
		mainProgressBar.setValue(mainProgressBar.getValue()+(1000000-barValue));
		progressItem.remove(cancelButton);
		createRemoveButton(progressItem);
	}

	private void createRemoveButton(ProgressItem progressItem) {
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(s -> {
			progressList.remove(progressItem);
			mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
			mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
		});
		progressItem.add(removeButton);
	}
    
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        private ProgressItem progressItem;
        private JProgressBar mainProgressBar;
        
        public Tracker(ProgressItem progressItem, JProgressBar mainProgressBar) {
			this.progressItem = progressItem;
			this.mainProgressBar = mainProgressBar;
		}

        public void setTotalProgress(int i) {
        	totalProgress = i;
        }
		/**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
        		SwingUtilities.invokeLater(() -> {
            	JProgressBar bar = progressItem.getProgressBar();
            	int ppmDelta2 = Math.min(ppmDelta, 1000000 - bar.getValue());
            	totalProgress += ppmDelta2;
            	mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta2);
            	bar.setValue(totalProgress);
        	});

        }
    }
}
