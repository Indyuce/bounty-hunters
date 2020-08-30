package net.Indyuce.bountyhunters.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import net.Indyuce.bountyhunters.BountyHunters;

public class NumberFormat {
	private final boolean thousands;

	private static final DecimalFormat digit1 = new DecimalFormat("0.#"), digit3 = new DecimalFormat("0.###");
	private static final String[] prefixes = { "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };
	private static final String[] prefixesk = { "K", "M", "B", "Tril", "Quad", "Quin", "Sext", "Sept", "Octi", "Noni", "Deci" };

	static {
		DecimalFormatSymbols symbols = digit1.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		digit1.setDecimalFormatSymbols(symbols);
		digit3.setDecimalFormatSymbols(symbols);
	}

	public NumberFormat() {
		this(false);
	}

	/**
	 * Formats a number (either 105.379M or 105379137.2 depending on the config
	 * option value). The highest power it can handle is 10^35 ie 100 decillion.
	 * Any number higher will display as 1000+ nonillion
	 * 
	 * @param thousands
	 *            If set to true, it will display 15.022K instead of 15022.
	 *            Whatever value it is set to, millions and higher will be
	 *            shortened as well
	 */
	public NumberFormat(boolean thousands) {
		this.thousands = thousands;
	}

	public String format(double d) {
		if (!BountyHunters.getInstance().formattedNumbers)
			return digit1.format(d);

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
