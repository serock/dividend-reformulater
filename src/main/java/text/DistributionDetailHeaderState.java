// SPDX-License-Identifier: MIT
package text;

class DistributionDetailHeaderState implements State {

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("\f")) {
            context.transitionToSearchState();
            return;
        }
        if (text.equals("Security description CUSIP and/or symbol State Date Amount Transaction type Notes")) {
            if (context.hasNoDistributionDetail()) {
                final String[] headers = new String[] {
                        "Security description",
                        "CUSIP",
                        "Symbol",
                        "State",
                        "Date",
                        "Amount",
                        "Transaction type",
                        "Notes",
                        "Quarter"
                };
                context.addDistributionDetailRow(headers);
            }
            context.transitionToDistributionDetailDataState();
        }
    }
}
