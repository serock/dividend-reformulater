// SPDX-License-Identifier: MIT
package text;

public class DistributionDetailHeaderState implements State {

    @Override
    public void accept(final Context context, final String text) {
        if (text.startsWith("Page")) {
            context.setState(new SearchState());
            return;
        }
        if (text.equals("Security description CUSIP and/or symbol State Date Amount Transaction type Notes")) {
            context.addDistributionDetailHeaderRowIfNeeded();
            context.setState(new DistributionDetailDataState());
        }
    }
}
