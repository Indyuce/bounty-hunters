package net.Indyuce.bountyhunters.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.Indyuce.bountyhunters.BountyHunters;

public class NumberFormat {
	private boolean thousands;

	static final DecimalFormat digit1 = new DecimalFormat("0.#"), digit3 = new DecimalFormat("0.###");
	static final String[] prefixes = { "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };
	static final String[] prefixesk = { "K", "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };

	static {
		DecimalFormatSymbols symbols = digit1.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		digit1.setDecimalFormatSymbols(symbols);
		digit3.setDecimalFormatSymbols(symbols);
	}

	public NumberFormat thousands() {
		thousands = true;
		return this;
	}

	public String format(double d) {
		if (!BountyHunters.getInstance().formattedNumbers)
			return "" + d;

		String[] array = thousands ? prefixesk : prefixes;
		int basePower = thousands ? 3 : 6;

		for (int j = array.length - 1; j >= 0; j--) {
			double b = Math.pow(10, basePower + 3 * j);
			if (d > b)
				return digit3.format(d / b) + array[j];
		}
		return digit1.format(d);
	}
}
