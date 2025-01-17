package joget.org;

import java.util.Map;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.joget.apps.app.service.AppUtil;

public class EdgeTheme extends AngleTheme {
    
    @Override
    public String getName() {
        return "Edge Theme";
    }

    @Override
    public String getVersion() {
        return "7.0.1";
    }

    @Override
    public String getLabel() {
        return getName();
    }
    
    @Override
    public String getDescription() {
        return "Edge Theme Based on Angle Theme";
    }
    
    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPathName() {
        return "progressive";
    }
    
    @Override
    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "/properties/edgeTheme.json", null, true, "/messages/angleTheme");
    }
    
    @Override
    public String getJsCssLib(Map<String, Object> data) {
       String path = data.get("context_path") + "/" + getPathName();

        String jsCssLink = "";
        jsCssLink += "<link href=\"" + data.get("context_path") + "/wro/" + getPathName() + ".preload.min.css" + "\" rel=\"stylesheet\" />\n";
        jsCssLink += "<link href=\"" + data.get("context_path") + "/plugin/"+getClassName()+"/angleTheme.css\" rel=\"stylesheet\" type=\"text/css\"/>\n";
        jsCssLink += "<link href=\"" + data.get("context_path") + "/plugin/"+getClassName()+"/form.css\" rel=\"stylesheet\" type=\"text/css\"/>\n";
        jsCssLink += "<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n";
        jsCssLink += "<link href=\"https://fonts.googleapis.com/css2?family=Source+Sans+Pro:wght@400;500;600&display=swap\" rel=\"stylesheet\">\n";
        jsCssLink += "<link rel=\"preload\" href=\"" + data.get("context_path") + "/js/fontawesome5/fonts/fontawesome-webfont.woff2?v=4.6.1\" as=\"font\" crossorigin />\n";
        jsCssLink += "<link rel=\"preload\" href=\"" + data.get("context_path") + "/js/fontawesome5/webfonts/fa-brands-400.woff2\" as=\"font\" crossorigin />\n";
        jsCssLink += "<link rel=\"preload\" href=\"" + data.get("context_path") + "/js/fontawesome5/webfonts/fa-solid-900.woff2\" as=\"font\" crossorigin />\n";
        jsCssLink += "<link rel=\"preload\" href=\"" + data.get("context_path") + "/universal/lib/material-design-iconic-font/fonts/Material-Design-Iconic-Font.woff2?v=2.2.0\" as=\"font\" crossorigin />\n";
        jsCssLink += "<script>loadCSS(\"" + data.get("context_path") + "/wro/" + getPathName() + ".min.css" + "\")</script>\n";
        
        jsCssLink += "<style>" + generateLessCss() + "</style>";

        jsCssLink += "<script src=\"" + data.get("context_path") + "/wro/" + getPathName() + ".min.js\" async></script>\n";
        if (enableResponsiveSwitch()) {
            jsCssLink += "<script src=\"" + data.get("context_path") + "/" + getPathName() +"/lib/responsive-switch.min.js\" defer></script>\n";
        } 
        jsCssLink += "<script>var _enableResponsiveTable = true;</script>\n";
        jsCssLink += getInternalJsCssLib(data);
            
        return jsCssLink;
    }
    
    @Override
    protected String getPrimaryColor() {
        String primary = "";
        if (!getPropertyString("dx8primaryColor").isEmpty()) {
            primary = getPropertyString("dx8primaryColor");
        }
        return primary;
    }
    
    @Override
    protected String generateLessCss() {
        String css = "";
        String lessVariables = "";
        String primary = getDefaultColor("primary");
        String accent = getDefaultColor("accent");
        String button = getDefaultColor("button");
        String buttonText = getDefaultColor("buttonText");
        String font = getDefaultColor("font");
        String menuFont = getDefaultColor("menuFont");
        String rowCountBackground = getDefaultColor("rowCountBackground");
        String dark = "darken(@primary , 10%)";
        String light = "lighten(@primary , 5%)";
        String lightAccent = "lighten(@accent , 10%)";
        String profileImageBackground = "#ffffff";
        String profileFontColor = "fade(@menuFont, 65%)";
        String formElementColor = "";
        
        
        if (!getPropertyString("dx8primaryColor").isEmpty()) {
            primary = getPropertyString("dx8primaryColor");
            light = "lighten(@primary , 6%)";
            dark = "darken(@primary , 10%)";
        }

        if (!getPropertyString("dx8linkColor").isEmpty()) {
            accent = getPropertyString("dx8linkColor");
        }

        if (!getPropertyString("dx8buttonBackground").isEmpty()) {
            button = getPropertyString("dx8buttonBackground");
        }
        
        if (!getPropertyString("dx8buttonColor").isEmpty()) {
            buttonText = getPropertyString("dx8buttonColor");
        }
        
        if (!getPropertyString("dx8fontColor").isEmpty()) {
            font = getPropertyString("dx8fontColor");
        }
        
        if (!getPropertyString("usermenu-style-background-image").isEmpty()) {
            profileImageBackground = "linear-gradient(rgba(0, 0, 0, 0.3),rgba(0, 0, 0, 0.3)),url(" + getPropertyString("usermenu-style-background-image") + ")";
            profileFontColor = "#ffffff";
        }
        
        if (!getPropertyString("dx8navLinkColor").isEmpty()) {
            menuFont = getPropertyString("dx8navLinkColor");
        }
        
        if (!getPropertyString("dx8menuRowCountColor").isEmpty()) {
            rowCountBackground = getPropertyString("dx8menuRowCountColor");
        }
        
        if (!getPropertyString("formElementColor").isEmpty()) {
            formElementColor = getPropertyString("formElementColor");
        }else{
            formElementColor = primary;
        }
        
        lessVariables += "@rowCountBackgroundColor: " + rowCountBackground + ";@profileFontColor: " + profileFontColor + "; @formElementColor: " + formElementColor + "; @profileBackgroundImage: " + profileImageBackground + "; @primary: " + primary + "; @darkPrimary: " + dark + "; @lightPrimary: " + light + "; @accent: " + accent + "; @lightAccent: " + lightAccent + "; @menuFont: " + menuFont + "; @button: " + button + "; @buttonText: " + buttonText + "; @defaultFontColor : " + font + ";";
        
        String less = AppUtil.readPluginResource(getClass().getName(), "resources/light.less");
        less = lessVariables + "\n" + less;
        // read CSS from cache
        Cache cache = (Cache) AppUtil.getApplicationContext().getBean("cssCache");
        if (cache != null) {
            Element element = cache.get(less);
            if (element != null) {
                css = (String) element.getObjectValue();
            }
        }
        if (css == null || css.isEmpty()) {
            // not available in cache, compile LESS
            css = compileLess(less);
            // store CSS in cache
            if (cache != null) {
                Element element = new Element(less, css);
                cache.put(element);
            }
        }
        return css;
    }
    
    @Override
    protected String getDefaultColor(String defaultColor) {
        if (defaultColor.equals("primary")) {
            defaultColor = "#016CF9";
        }
        else if (defaultColor.equals("accent")) {
            defaultColor = "#0D6EFD";
        }
        else if (defaultColor.equals("button")) {
            defaultColor = "#016CF9";
        }
        else if (defaultColor.equals("buttonText")) {
            defaultColor = "#FFFFFF";
        }
        else if (defaultColor.equals("menuFont")) {
            defaultColor = "#000000";
        }
        else if (defaultColor.equals("font")) {
            defaultColor = "#FFFFFF";
        }
        else if (defaultColor.equals("rowCountBackground")) {
            defaultColor = "#27c24c";
        }
        return defaultColor;
    }
}
