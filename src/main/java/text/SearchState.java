// SPDX-License-Identifier: MIT
package text;

public class SearchState implements State {

    @Override
    public void accept(final Context context, final String text) {
        if (text.endsWith("Detail for Dividends and Distributions")) {
            context.setState(new DistributionDetailHeaderState());
        } else if (text.endsWith("Mutual Fund and UIT Supplemental Information") || text.contains("End Notes")) {
            context.setState(new SupplementalInfoState());
        } else if (text.startsWith("Instructions for Recipient")) {
            context.setState(new Form1099DivState());
        }
    }
}
