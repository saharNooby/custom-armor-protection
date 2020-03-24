package me.saharnooby.plugins.customarmor.config;

import lombok.NonNull;

/**
 * @author saharNooby
 * @since 15:25 16.03.2020
 */
public final class Amount {

	private final double value;
	private final boolean isRelative;

	public Amount(@NonNull String s) {
		this.isRelative = s.endsWith("%");

		if (s.endsWith("%")) {
			s = s.substring(0, s.length() - 1);
		}

		try {
			this.value = Double.parseDouble(s) / (this.isRelative ? 100 : 1);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid value '" + s +"', must be a number with optional percent sign");
		}
	}

	public double apply(double x) {
		return this.isRelative ? x * (1 + this.value) : x + this.value;
	}

	@Override
	public String toString() {
		return this.isRelative ? (this.value * 100) + "%" : this.value + "";
	}

}
