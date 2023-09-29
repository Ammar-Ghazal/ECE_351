/* *********************************************************************
 * ECE351 
 * Department of Electrical and Computer Engineering 
 * University of Waterloo 
 * Term: Fall 2021 (1219)
 *
 * The base version of this file is the intellectual property of the
 * University of Waterloo. Redistribution is prohibited.
 *
 * By pushing changes to this file I affirm that I am the author of
 * all changes. I affirm that I have complied with the course
 * collaboration policy and have not plagiarized my work. 
 *
 * I understand that redistributing this file might expose me to
 * disciplinary action under UW Policy 71. I understand that Policy 71
 * allows for retroactive modification of my final grade in a course.
 * For example, if I post my solutions to these labs on GitHub after I
 * finish ECE351, and a future student plagiarizes them, then I too
 * could be found guilty of plagiarism. Consequently, my final grade
 * in ECE351 could be retroactively lowered. This might require that I
 * repeat ECE351, which in turn might delay my graduation.
 *
 * https://uwaterloo.ca/secretariat-general-counsel/policies-procedures-guidelines/policy-71
 * 
 * ********************************************************************/

package ece351.w.svg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.parboiled.common.ImmutableList;

import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;


public final class TransformSVG2W {
	
	/**
	 * Transforms an instance of WSVG to an instance of WProgram.
	 * Write this algorithm in whatever way you wish.
	 * Remember that the AST is immutable.
	 * You might want to build up some mutable temporary structures.
	 * ImmutableList can be used as a "mutable" temporary structure if the 
	 * local variable is not final: just re-assign the local variable to the new list.
	 * 
	 * We used to give more detailed comments on the staff algorithm,
	 * but many students in several offerings of this course found
	 * those comments confusing, and asked for them to be removed.
	 * 
	 * @see #COMPARE_Y_X 
	 * @see #transformLinesToWaveform(List, List)
	 * @see java.util.ArrayList
	 * @see java.util.LinkedHashSet
	 */
	public static final WProgram transform(final PinsLines pinslines) {
		final List<Line> lines = new ArrayList<Line>(pinslines.segments);
		final List<Pin> pins = new ArrayList<Pin>(pinslines.pins);

		// Sort all lines by y value then x value:
		Collections.sort(lines, COMPARE_Y_X);

		// Use ArrayList and LinkedHashSet (for storing y values):
		// Initialize variables: waveforms, yVals, and removedVals
		ImmutableList<Waveform> waveforms = ImmutableList.of();
		final Set<Integer> yVals = new LinkedHashSet<Integer>();
		final List<Line> removedVals = new ArrayList<Line>();
		// - yVals and removedVals so that we can keep track of values to consume, 
		// and values that have been consumed
		
		// Add all lines to yVals:
		yVals.add(lines.get(0).y1);

		// Go through all lines, until we have no more lines:
		while(!lines.isEmpty()) {
			// Get current line from lines
			final Line currentLine = lines.get(0);

			if (yVals.contains(currentLine.y1) && !yVals.contains(currentLine.y2)){
				// Only y1 is contained in current line:
				removedVals.add(currentLine);
				yVals.add(currentLine.y2);
				lines.remove(0);
			} else if(yVals.contains(currentLine.y1)){
				// Both y1 and y2 are contained in current line:
				removedVals.add(currentLine);
				lines.remove(0);
			} else if(yVals.contains(currentLine.y2)){
				// Only y2 is contained in current line:
				removedVals.add(currentLine);
				yVals.add(currentLine.y1);
				lines.remove(0);
			}else{
				// Neither y1 nor y2 are contained in current line:
				waveforms = waveforms.append(transformLinesToWaveform(removedVals, pins));
				pins.remove(0);
				removedVals.clear();
				yVals.clear();
				yVals.add(currentLine.y1);
			}
		}

		// Add last waveform, and return:
		waveforms = waveforms.append(transformLinesToWaveform(removedVals, pins));
		return new WProgram(waveforms);
	}

	/**
	 * Transform a list of Line to an instance of Waveform.
	 * The concept of a y-midpoint might be useful: 1 is a line above; 0 is a line below.
	 * What to do about "dots"?
	 * ImmutableList can be used as a "mutable" temporary structure if the 
	 * local variable is not final: just re-assign the local variable to the new list.
	 * 
	 * We used to give more detailed comments on the staff algorithm,
	 * but many students in several offerings of this course found
	 * those comments confusing, and asked for them to be removed.
	 * 
	 * @see #COMPARE_X
	 * @see #transform(PinsLines)
	 * @see Pin#id
	 */
	private static Waveform transformLinesToWaveform(final List<Line> lines, final List<Pin> pins) {
		if(lines.isEmpty()) return null;

		// Sort all lines by x values:
		Collections.sort(lines, COMPARE_X);

		// Initialize variables: start of waveform, data, list
		final Line start = lines.get(0);
		ImmutableList<String> data = ImmutableList.of();
		ArrayList<String> list = new ArrayList<>();

		// Iterate through all lines:
		for(int i = 1; i <= lines.size() - 1; i++) {
			// Get current line:
			final Line current = lines.get(i);

			// Make sure line is now different:
			if (current.x1 != current.x2){
				// If y level changed, send corresponding waveform value:
				if (current.y2 < start.y1){
					list.add("1");
				}else{
					list.add("0");
				}
			}
		}

		// To construct waveform, we need to send over the data and an ID:
		data = ImmutableList.copyOf(list);
		String ID = pins.get(0).id;
		return new Waveform(data, ID);

		// // TODO: longer code snippet
		// throw new ece351.util.Todo351Exception();
	}

	/**
	 * Sort a list of lines according to their x position.
	 * 
	 * @see java.util.Comparator
	 */
	public final static Comparator<Line> COMPARE_X = new Comparator<Line>() {
		@Override
		public int compare(final Line l1, final Line l2) {
			if(l1.x1 < l2.x1) return -1;
			if(l1.x1 > l2.x1) return 1;
			if(l1.x2 < l2.x2) return -1;
			if(l1.x2 > l2.x2) return 1;
			return 0;
		}
	};

	/**
	 * Sort a list of lines according to their y position first, and then x position second.
	 * 
	 * @see java.util.Comparator
	 */
	public final static Comparator<Line> COMPARE_Y_X = new Comparator<Line>() {
		@Override
		public int compare(final Line l1, final Line l2) {
			final double y_mid1 = (double) (l1.y1 + l1.y2) / 2.0f;
			final double y_mid2 = (double) (l2.y1 + l2.y2) / 2.0f;
			final double x_mid1 = (double) (l1.x1 + l1.x2) / 2.0f;
			final double x_mid2 = (double) (l2.x1 + l2.x2) / 2.0f;
			if (y_mid1 < y_mid2) return -1;
			if (y_mid1 > y_mid2) return 1;
			if (x_mid1 < x_mid2) return -1;
			if (x_mid1 > x_mid2) return 1;
			return 0;
		}
	};

}
