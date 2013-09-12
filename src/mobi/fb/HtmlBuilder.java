/*
 */
package mobi.fb;

/**
 *
 * @author evan.summers
 */
public class HtmlBuilder {
    StringBuilder builder = new StringBuilder();
    
    public HtmlBuilder(String content) {
        builder.append(content);
    }

    public void replace(String pattern, Object value) {
        int index = builder.indexOf(pattern);
        if (index > 0 && builder.charAt(index - 1) == '$')
        builder.replace(index - 1, index + pattern.length(), value.toString());
    }
    
    @Override
    public String toString() {
        return builder.toString();
    }
    
}
