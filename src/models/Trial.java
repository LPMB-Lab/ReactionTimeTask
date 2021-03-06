package models;

/*
 *   FINGERS:
 *   ==========
 *   LEFT HAND:
 *   -4 : Pinky
 *   -3 : Ring
 *   -2 : Middle
 *   -1 : Index
 *   
 *   RIGHT HAND:
 *   +1 : Index
 *   +2 : Middle
 *   +3 : Ring
 *   +4 : Pinky
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Trial {

	private int[] m_aRawEntries = new int[20];
	private int[] m_aEntries = new int[20];
	private long[] m_aTimers = new long[20];
	private int[] m_aIntraMirror = new int[4];
	private int[] m_aErrorFingerPressed = new int[8];
	private int m_iSameHandErrors;
	private int m_iOppositeHandErrors;
	private int m_iIntraSwapMirrorCount;
	private int m_iAvgInterSwitchTime;
	private int m_iAvgIntraSwitchTime;
	private int m_iAvgInterHomologousSwitchTime;
	private int m_iAvgInterNonHomologousSwitchTime;
	private int m_iAvgLeftToRightSwitchTime;
	private int m_iAvgRightToLeftSwitchTime;
	private long m_iFastestTime;

	public Trial() {
		for (int i = 0; i < m_aEntries.length; i++) {
			m_aTimers[i] = 0;
		}

		for (int i = 0; i < m_aErrorFingerPressed.length; i++) {
			m_aErrorFingerPressed[i] = 0;
		}

		m_iSameHandErrors = 0;
		m_iOppositeHandErrors = 0;
		m_iAvgInterSwitchTime = 0;
		m_iAvgIntraSwitchTime = 0;
		m_iAvgInterHomologousSwitchTime = 0;
		m_iAvgInterNonHomologousSwitchTime = 0;
		m_iAvgLeftToRightSwitchTime = 0;
		m_iAvgRightToLeftSwitchTime = 0;
		m_iIntraSwapMirrorCount = 0;
		GenerateTrials();
	}

	/* PRIVATE FUNCTIONS */
	private void GenerateTrials() {
		ArrayList<Integer> MirrorSwapNumbers = new ArrayList<Integer>();
		ArrayList<Integer> EntryNumbers = new ArrayList<Integer>();

		for (int i = 1; i < 9; i++)
			MirrorSwapNumbers.add(i);

		// Add Interswaps as 0s
		for (int i = 1; i < 9; i++)
			EntryNumbers.add(0);

		// Add Intraswaps as 1s
		for (int i = 1; i < 12; i++)
			EntryNumbers.add(1);

		Collections.shuffle(EntryNumbers);
		Collections.shuffle(MirrorSwapNumbers);

		for (int i = 0; i < m_aIntraMirror.length; i++)
			m_aIntraMirror[i] = MirrorSwapNumbers.get(i);

		EntryNumbers.add(0, 0);

		for (int i = 0; i < m_aEntries.length; i++) {
			m_aRawEntries[i] = EntryNumbers.get(i);
			m_aEntries[i] = EntryNumbers.get(i);
		}

		for (int i = 0; i < m_aEntries.length; i++) {
			if (i == 0) {
				if (new Random().nextInt(2) == 0)
					m_aEntries[i] = RandomFingerRight(0);
				else
					m_aEntries[i] = RandomFingerLeft(0);
			} else {
				if (isLeftHand(m_aEntries[i - 1])) {
					if (isInterSwap(m_aEntries[i])) {
						m_iIntraSwapMirrorCount++;
						boolean isMirror = false;

						for (int j = 0; j < m_aIntraMirror.length; j++) {
							if (m_aIntraMirror[j] == m_iIntraSwapMirrorCount) {
								m_aEntries[i] = m_aEntries[i - 1] * -1;
								isMirror = true;
							}
						}

						if (!isMirror)
							m_aEntries[i] = RandomFingerRight(m_aEntries[i - 1]);
					} else
						m_aEntries[i] = RandomFingerLeft(m_aEntries[i - 1]);
				} else {
					if (isInterSwap(m_aEntries[i])) {
						m_iIntraSwapMirrorCount++;
						boolean isMirror = false;

						for (int j = 0; j < m_aIntraMirror.length; j++) {
							if (m_aIntraMirror[j] == m_iIntraSwapMirrorCount) {
								m_aEntries[i] = m_aEntries[i - 1] * -1;
								isMirror = true;
							}
						}

						if (!isMirror)
							m_aEntries[i] = RandomFingerLeft(m_aEntries[i - 1]);
					} else
						m_aEntries[i] = RandomFingerRight(m_aEntries[i - 1]);
				}
			}
		}
	}

	private int RandomFingerRight(int previousID) {
		int newFinger;

		if (previousID < 0)
			previousID *= -1;

		do
			newFinger = new Random().nextInt(4) + 1;
		while (newFinger == previousID);

		return newFinger;
	}

	private int RandomFingerLeft(int previousID) {
		int newFinger;

		if (previousID < 0)
			previousID *= -1;

		do
			newFinger = (new Random().nextInt(4) + 1);
		while (newFinger == previousID);

		return newFinger * -1;
	}

	private boolean isLeftHand(int id) {
		return id < 0 ? true : false;
	}

	private boolean isInterSwap(int id) {
		return id == 0 ? true : false;
	}

	/* PUBLIC FUNCTIONS */
	public void setTimer(int step, long time) {
		m_aTimers[step] = time;

		if (step == 19) {
			m_iFastestTime = m_aTimers[1];
			for (int i = 1; i < m_aTimers.length; i++) {
				if (m_aTimers[i] < m_iFastestTime)
					m_iFastestTime = m_aTimers[i];
			}

			for (int i = 1; i < m_aRawEntries.length; i++) {
				if (m_aRawEntries[i] == 1)
					m_iAvgIntraSwitchTime += m_aTimers[i];
				else if (m_aRawEntries[i] == 0)
					m_iAvgInterSwitchTime += m_aTimers[i];
			}

			int count = 0;
			boolean isHomologous;

			for (int i = 1; i < m_aRawEntries.length; i++) {
				isHomologous = false;

				if (m_aRawEntries[i] == 0) {
					count++;
					for (int j = 0; j < m_aIntraMirror.length; j++) {
						if (m_aIntraMirror[j] == count)
							isHomologous = true;
					}

					if (isHomologous)
						m_iAvgInterHomologousSwitchTime += m_aTimers[i];
					else
						m_iAvgInterNonHomologousSwitchTime += m_aTimers[i];
				}
			}

			for (int i = 1; i < m_aRawEntries.length; i++) {
				if (m_aRawEntries[i] == 0) {
					if (m_aEntries[i - 1] > 0) {
						m_iAvgRightToLeftSwitchTime += m_aTimers[i];
					} else {
						m_iAvgLeftToRightSwitchTime += m_aTimers[i];
					}
				}
			}

			m_iAvgInterHomologousSwitchTime /= 4;
			m_iAvgInterNonHomologousSwitchTime /= 4;
			m_iAvgInterSwitchTime /= 8;
			m_iAvgIntraSwitchTime /= 11;
			m_iAvgRightToLeftSwitchTime /= 4;
			m_iAvgLeftToRightSwitchTime /= 4;
		}
	}
	
	public int getCurrentFinger(int index) {
		return m_aEntries[index];
	}

	public void setErrorFinger(int fingerIndex) {
		m_aErrorFingerPressed[fingerIndex]++;
	}
	
	public void setErrorHand(boolean sameHand) {
		if (sameHand) {
			m_iSameHandErrors++;
		} else {
			m_iOppositeHandErrors++;
		}
	}
	
	public void UpdateErrorTotals(double[] errorTotals) {
		for (int i = 0; i < m_aErrorFingerPressed.length; i ++) {
			errorTotals[i] += m_aErrorFingerPressed[i];
		}
		
		errorTotals[8] += m_iSameHandErrors;
		errorTotals[9] += m_iOppositeHandErrors;
	}
	
	public void UpdateTotals(double[] trialTotals) {
		trialTotals[0] += m_iAvgInterSwitchTime;
		trialTotals[1] += m_iAvgIntraSwitchTime;
		trialTotals[2] += m_iAvgInterHomologousSwitchTime;
		trialTotals[3] += m_iAvgInterNonHomologousSwitchTime;
		trialTotals[4] += m_iAvgRightToLeftSwitchTime;
		trialTotals[5] += m_iAvgLeftToRightSwitchTime;
		trialTotals[6] += m_iFastestTime;
	}

	public String ExportTrialRaw() {
		String exportString = "";
		String tabl = "\t";
		String endl = "\r\n";

		exportString += "Timings:" + tabl;

		for (int i = 0; i < m_aTimers.length; i++) {
			if (i == 19)
				exportString += m_aTimers[i] + endl;
			else
				exportString += m_aTimers[i] + tabl;
		}

		exportString += "Fingers:" + tabl;

		for (int i = 0; i < m_aEntries.length; i++) {
			if (i == 19)
				exportString += m_aEntries[i] + endl;
			else
				exportString += m_aEntries[i] + tabl;
		}

		exportString += "Raw Fingers:" + tabl;

		for (int i = 0; i < m_aRawEntries.length; i++) {
			if (i == 19)
				exportString += m_aRawEntries[i] + endl;
			else
				exportString += m_aRawEntries[i] + tabl;
		}

		exportString += endl;

		return exportString;
	}
	
	public String ExportErrorTrial() {
		String exportString = "";
		String tabl = "\t";
		String endl = "\r\n";
		
		for (int i = 0; i < m_aErrorFingerPressed.length; i++) {
			exportString += m_aErrorFingerPressed[i] + tabl;
		}
		
		exportString += m_iSameHandErrors + tabl;
		exportString += m_iOppositeHandErrors + tabl;
		exportString += endl;
		
		
		
		return exportString;
	}

	public String ExportTrial() {
		String exportString = "";
		String tabl = "\t";
		String endl = "\r\n";

		exportString += m_iAvgInterSwitchTime + tabl;
		exportString += m_iAvgIntraSwitchTime + tabl;
		exportString += m_iAvgInterHomologousSwitchTime + tabl;
		exportString += m_iAvgInterNonHomologousSwitchTime + tabl;
		exportString += m_iAvgRightToLeftSwitchTime + tabl;
		exportString += m_iAvgLeftToRightSwitchTime + tabl;
		exportString += m_iFastestTime + endl;

		return exportString;
	}
}