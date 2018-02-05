package net.pieroxy.ua.detection;
enum MatchingRegion {
    REGULAR(true,false),
    PARENTHESIS(false,true),
    BOTH(true,true),
    CONSUMED(false,false);

    private boolean paren;
    private boolean regular;
    public boolean includesParenthesis() {
        return paren;
    }
    public boolean includesRegular() {
        return regular;
    }
    private MatchingRegion(boolean r, boolean p) {
        paren = p;
        regular = r;
    }
}