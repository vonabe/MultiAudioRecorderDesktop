package waveplayer;
/************************************************************************
 * Name        : 
 * Username    : 
 * Description : 
 *************************************************************************/

import java.awt.*;
import javax.swing.*;

public class WavPanel extends JPanel 
{
	// Holds the currently loaded audio data
	private double [] audio = null;
	
	// Present playback position, -1 if not currently playing
	private int currentIndex = -1;

	// Attempts to load the audio in the given filename.
	// Returns true on success, false otherwise.
	public boolean load(String filename)
	{
		return false;
	}

	// Return the number of samples in the currently loaded audio.
	// Returns 0 if no audio loaded.
	public int getNumSamples()
	{
		return 0;
	}

	// Get the index of the next audio sample that will be played.
	// Returns -1 if playback isn't active.
	public int getCurrentIndex()
	{	
		return 0;
	}

	// Sets the index of the next audio sample to be played.
	// Set to -1 when playback is not active.
	public void setCurrentIndex(int i)
	{
	}

	// Play a single audio sample based on the current index.
	// Advance the index one position.
	// Returns the panel x-coordinate of the played sample.
	// Returns -1 if playback failed (no audio or index out of range).
	public int playAndAdvance()
	{
		return 0;
	}

	// Draw the waveform and the current playing position (if any)
	// This method shouldn't be called directly.  The player
	// should call repaint() whenever you need to update the 
	// waveform visualization.
	public void paintComponent(Graphics g) 
	{
	}

}
