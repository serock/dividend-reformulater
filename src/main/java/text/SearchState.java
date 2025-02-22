// SPDX-License-Identifier: MIT
package text;

class SearchState implements State {

    @Override
    public void accept(final Context context, final String text) {
        if (text.endsWith("Detail for Dividends and Distributions")) {
            context.transitionToDistributionDetailHeaderState();
        } else if (text.endsWith("Mutual Fund and UIT Supplemental Information") || text.contains("End Notes")) {
            context.transitionToSupplementalInfoState();
        } else if (text.startsWith("Instructions for Recipient")) {
            context.transitionToForm1099DivState();
        }
    }
}
