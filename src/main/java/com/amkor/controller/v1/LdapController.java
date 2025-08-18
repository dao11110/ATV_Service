package com.amkor.controller.v1;


import com.amkor.models.LoginModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;

@RestController
public class LdapController {

    // @Autowired
    // private static String ldapUserId = "";
    // private static String ldapPasswd = ;
    // private static String basedn = "DC=kr,DC=ds,DC=amkor,DC=com";
    // private static String ldapPathFormat = "ldap://10.141.10.23:3268";
    //389 포트로 잡속하면 간헐적으로 연결 안됨 timeout error 발생
    //Connect to the Global Catalog (GC) port 3268 instead of using the standard LDAP port 389 if the Domain Controller is also the Global Catalog server.
    //The Global Catalog will have a copy of all the Active Directory (AD) objects in the domain, which allows the correct authentication.
    //If using a load balancer, this port will need to be opened on the load balancing appliance.
    //"ldap://k1wkrdcp01.kr.ds.amkor.com:389";
    // JSONObject jsonObject = new JSONObject();

    String ret = "false";

    public static String getLdapAddressBySite(String site) {
        String ldapPathFormat = "";

        switch (site) {
            case "ALL":
                ldapPathFormat = "LDAP://Adldap.amkor.com:3268";
                break;
            case "ATK":
                ldapPathFormat = "LDAP://k5wkrdcp01.kr.ds.amkor.com:3268";
                break;
            case "ATI":
                ldapPathFormat = "LDAP://AWUSDCP04.us.ds.amkor.com:3268";//AWUSDCP05.us.ds.amkor.com
                break;
            case "ATJ":
                ldapPathFormat = "LDAP://Adldap.amkor.com:3268";
                // ldapPathFormat = "LDAP://JKTJPDCP01.JP.DS.AMKOR.COM:3268";//LDAP://JKTJPDCP02.JP.DS.AMKOR.COM:3268
                break;
            case "ATC":
                ldapPathFormat = "LDAP://C3WCNDCP01.CN.DS.AMKOR.COM:3268";//LDAP://C3WCNDCP02.CN.DS.AMKOR.COM:3268
                break;
            case "ATM":
                ldapPathFormat = "LDAP://MWMYDCP01.MY.DS.AMKOR.COM:3268";
                break;
            case "ATP":
                ldapPathFormat = "LDAP://P1WPHDCP01.PH.DS.AMKOR.COM:3268";
                break;
            case "ATT":
                ldapPathFormat = "LDAP://T1WTWDCP01.TW.DS.AMKOR.COM:3268";
                break;
            case "ATEP":
                ldapPathFormat = "LDAP://PTWDCP01.eu.ds.amkor.com:3268";
                break;
            case "ATV":
                ldapPathFormat = "LDAP://V1WVNDCP01.vn.ds.amkor.com:3268";//"LDAP://V1WVNDCP02.vn.ds.amkor.com:3268";
                break;
            default:
                ldapPathFormat = "LDAP://Adldap.amkor.com:3268";
                break;
        }

        return ldapPathFormat;
    }

    public static String getLdapDomainBySite(String site) {
        String ldapDomain = "";

        switch (site) {
            case "ATK":
                ldapDomain = "@kr.ds.amkor.com";
                break;
            case "ATI":
                ldapDomain = "@us.ds.amkor.com";
                break;
            case "ATJ":
                ldapDomain = "@jp.ds.amkor.com";
                break;
            case "ATC":
                ldapDomain = "@cn.ds.amkor.com";
                break;
            case "ATM":
                ldapDomain = "@my.ds.amkor.com";
                break;
            case "ATP":
                ldapDomain = "@ph.ds.amkor.com";
                break;
            case "ATT":
                ldapDomain = "@tw.ds.amkor.com";
                break;
            case "ATEP":
                ldapDomain = "@eu.ds.amkor.com";
                break;
            case "ATV":
                ldapDomain = "@vn.ds.amkor.com";
                break;
            default:
                break;
        }

        return ldapDomain;
    }

    public static String getLdapBaseBySite(String site) {
        String ldapBasedn = "";

        switch (site) {
            case "ALL":
                ldapBasedn = "DC=com";
                break;
            case "ATK":
                ldapBasedn = "DC=kr,DC=ds,DC=amkor,DC=com";
                break;
            case "ATI":
                ldapBasedn = "DC=us,DC=ds,DC=amkor,DC=com";
                break;
            case "ATJ":
                ldapBasedn = "DC=jp,DC=ds,DC=amkor,DC=com";
                break;
            case "ATC":
                ldapBasedn = "DC=cn,DC=ds,DC=amkor,DC=com";
                break;
            case "ATM":
                ldapBasedn = "DC=MY,DC=DS,DC=AMKOR,DC=Com";
                break;
            case "ATP":
                ldapBasedn = "DC=ph,DC=ds,DC=amkor,DC=com";
                break;
            case "ATT":
                ldapBasedn = "DC=tw,DC=ds,DC=amkor,DC=com";
                break;
            case "ATEP":
                ldapBasedn = "DC=eu,DC=ds,DC=amkor,DC=com";
                break;
            case "ATV":
                ldapBasedn = "DC=vn,DC=ds,DC=amkor,DC=com";
                break;
            default:
                ldapBasedn = "DC=com";
                break;
        }

        return ldapBasedn;
    }

    public String getDomainName() {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        return req.getServerName();
    }

    public Hashtable<String, String> getProperties(String path) {

        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        properties.put(Context.PROVIDER_URL, path);
        properties.put(Context.SECURITY_AUTHENTICATION, "simple");
        properties.put(Context.SECURITY_PRINCIPAL, "pwreset@kr.ds.amkor.com");
        properties.put(Context.SECURITY_CREDENTIALS, "Dq@9qWvcip");
        properties.put("java.naming.referral", "follow");

        return properties;
    }

    public static boolean isNumeric(String s) {
        return s.chars().allMatch(Character::isDigit);
    }

    // Amkor AD LDAP Login
    @RequestMapping(method = RequestMethod.POST, value = "/ldap/login")
    public LoginModel Ldap_login(
            @RequestParam("site") String site,
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws Exception {

        LoginModel userVO = new LoginModel();

        String path = String.format(getLdapAddressBySite(site));
        byte[] decoded_id = Base64.getDecoder().decode(username.getBytes());
        String login_id = new String(decoded_id);
        byte[] decoded_pw = Base64.getDecoder().decode(password.getBytes());
        String login_pwd = new String(decoded_pw, "UTF-8");
        String filterString = "sAMAccountName=" + new String(decoded_id);
        ret = "false";

        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);
            boolean results_flag = false;
            results_flag = results.hasMore();

            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                // System.out.println(attrs);

                userVO.setSite(site);
                userVO.setSAMAccountName(nullCheck(attrs.get("sAMAccountName"), "sAMAccountName", attrs));
                userVO.setDisplayname(nullCheck(attrs.get("displayname"), "displayname", attrs));
                userVO.setEmail(nullCheck(attrs.get("mail"), "mail", attrs));
                userVO.setOffice(
                        nullCheck(attrs.get("physicalDeliveryOfficeName"), "physicalDeliveryOfficeName", attrs));
                userVO.setDepartment(nullCheck(attrs.get("department"), "department", attrs));
                userVO.setPosition(nullCheck(attrs.get("title"), "title", attrs));

                if (site.equals("ATK") || site.equals("ATV")) {
                    userVO.setPager(nullCheck(attrs.get("pager"), "pager", attrs));
                } else {
                    String sa = nullCheck(attrs.get("sAMAccountName"), "sAMAccountName", attrs);
                    if (isNumeric(sa)) {
                        userVO.setPager(sa);
                    } else {
                        userVO.setPager("");
                    }
                }

                userVO.setSn(nullCheck(attrs.get("sn"), "sn", attrs));
                userVO.setTelephoneNumber(nullCheck(attrs.get("telephoneNumber"), "telephoneNumber", attrs));

                if (site.equals("ATK")) {
                    userVO.setPlant(nullCheck(attrs.get("company"), "company", attrs));
                } else {
                    userVO.setPlant(getPlantBySite(site));
                }
            }
            if (!getDomainName().equals("localhost1")) {
                if (results_flag)
                    ret = String.valueOf(
                            isAuthenticatedUser(site, login_id, login_pwd)); //userVO.getSAMAccountName().trim(), new String(decoded_pw, "UTF-8")));
                if (!ret.trim().equals("true")) { //password bypass
                    userVO = new LoginModel();
                }
            }
        } catch (NamingException e) {
            System.out.println(userVO.toString());
            e.printStackTrace();
        }

        return userVO;
    }

    public static String getPlantBySite(String site) {
        String plant = "";
        if (site.equals("ATV")) plant = "V1";
        else if (site.equals("ATC")) plant = "C3";
        else plant = site;
        return plant;
    }

    public String nullCheck(Attribute attribute, String name, Attributes attrs) {

        if (attribute == null) {
            return "";
        } else {
            return attrs.get(name).toString().substring(attrs.get(name).toString().indexOf(":") + 1).trim();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ldap/existAccount")
    public boolean LdapExistAccount(
            @RequestParam("site") String site,
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws Exception {

        byte[] decoded_id = Base64.getDecoder().decode(username.trim());
        String path = String.format(getLdapAddressBySite(site));

        String filterString = "pager=" + new String(decoded_id);
        DirContext context = null;
        boolean results_flag = false;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            results_flag = results.hasMore();

        } catch (NamingException e) {
            e.printStackTrace();
        }
        return results_flag;
    }

    public static boolean isAuthenticatedUser(String site, String userId, String password) throws Exception {

        boolean isAuthenticated = false;
        String path = String.format(getLdapAddressBySite(site));
        String ldapDomain = getLdapDomainBySite(site);

        if (password != null && password != "") {
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            properties.put(Context.PROVIDER_URL, path);
            properties.put(Context.SECURITY_AUTHENTICATION, "simple");
            properties.put(Context.SECURITY_PRINCIPAL, userId + ldapDomain);
            properties.put(Context.SECURITY_CREDENTIALS, password.trim());
            properties.put("java.naming.referral", "follow");

            try {
                DirContext con = new InitialDirContext(properties);
                con.close();
                isAuthenticated = true;

            } catch (NamingException e) {
                System.out.println(e);

                try {
                    HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();
                    String ip = req.getRemoteAddr();
                    InetAddress addr = InetAddress.getByName(ip);
                    String host = addr.getHostName();
                    System.out.println(" - ID:" + userId + " - IP:" + ip + " - HOST:" + host);
                } catch (Exception ee) {
                    System.out.println(ee);
                }
            }
        }
        //System.out.println(isAuthenticated);
        return isAuthenticated;
    }

    //ATK는 한글이름, 그외는 영문명으로 검색
    @RequestMapping(method = RequestMethod.POST, value = "/ldap/search")
    public ArrayList<LoginModel> Ldap_search_ww(
            @RequestParam("site") String site,
            @RequestParam("username") String username) throws Exception {

        String filterString = "";
        if (site.equals("ATK")) {
            filterString = "(sn=" + username.trim() + "*)";
        } else {
            filterString = "displayname=" + username.trim() + "*";
        }
        ret = "false";

        return ldapSearchDisplayname(site, filterString);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/ldap/searchByID")
    public ArrayList<LoginModel> LdapSearchByID(String site, String id) {

        String filterString = "";
        if (site.equals("ATC")) {
            filterString = "(sAMAccountName=" + id.trim() + "*)";
        } else
            filterString = "(pager=" + id.trim() + "*)";

        ret = "false";

        return ldapSearchDisplayname(site, filterString);
    }

    //한글이름으로 검색
    @RequestMapping(method = RequestMethod.POST, value = "/ldap/approval")
    public ArrayList<LoginModel> Ldap_search_atk(
            @RequestParam("username") String username) throws Exception {

        String filterString = "(sn=" + username.trim() + "*)";
        ret = "false";

        return ldapSearchDisplayname("ATK", filterString);
    }

    //영문이름으로 검색//wi/iforecast
    @RequestMapping(method = RequestMethod.POST, value = "/ldap/search/displayname")
    public ArrayList<LoginModel> Ldap_search_displayname(
            @RequestParam("site") String site,
            @RequestParam("username") String username) throws Exception {

        String filterString = "displayname=" + username.trim() + "*";
        ret = "false";

        return ldapSearchDisplayname(site, filterString);
    }

    public ArrayList<LoginModel> ldapSearchDisplayname(String site, String filterString) {

        LoginModel userVO = new LoginModel();
        String path = String.format(getLdapAddressBySite(site));
        ArrayList<LoginModel> list = new ArrayList<LoginModel>();

        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            while (results.hasMore()) {

                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                userVO = new LoginModel();
                userVO.setSite(site);
                userVO.setSAMAccountName(nullCheck(attrs.get("sAMAccountName"), "sAMAccountName", attrs).trim());
                userVO.setDisplayname(nullCheck(attrs.get("displayname"), "displayname", attrs).trim());
                userVO.setEmail(nullCheck(attrs.get("mail"), "mail", attrs).trim());
                userVO.setOffice(
                        nullCheck(attrs.get("physicalDeliveryOfficeName"), "physicalDeliveryOfficeName", attrs).trim());
                userVO.setDepartment(nullCheck(attrs.get("department"), "department", attrs).trim());
                userVO.setPosition(nullCheck(attrs.get("title"), "title", attrs).trim());

                if (site.equals("ATK") || site.equals("ATV")) {
                    userVO.setPager(nullCheck(attrs.get("pager"), "pager", attrs));
                } else {
                    String sa = nullCheck(attrs.get("sAMAccountName"), "sAMAccountName", attrs);
                    if (isNumeric(sa)) {
                        userVO.setPager(sa);
                    } else {
                        userVO.setPager("");
                    }
                }

                userVO.setSn(nullCheck(attrs.get("sn"), "sn", attrs).trim());
                userVO.setTelephoneNumber(nullCheck(attrs.get("telephoneNumber"), "telephoneNumber", attrs).trim());
                if (site.equals("ATK")) {
                    userVO.setPlant(nullCheck(attrs.get("company"), "company", attrs));
                } else {
                    userVO.setPlant(getPlantBySite(site));
                }

                list.add(userVO);
            }
        } catch (NamingException | NullPointerException e) {
            e.printStackTrace();
        }
        return list;
    }

    //carry
    @RequestMapping(method = RequestMethod.GET, value = "/ldap/badge/{site}/{pager}")
    public HashMap<String, String> ldap_by_badge(@PathVariable String site, @PathVariable String pager) throws Exception {

        HashMap<String, String> userInfo = new HashMap<>();
        String path = String.format(getLdapAddressBySite(site));
        String filterString = "pager=" + pager.trim();

        ret = "false";
        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();

                userInfo.put("name", nullCheck(attrs.get("displayname"), "displayname", attrs));
                userInfo.put("account_id", nullCheck(attrs.get("samaccountName"), "samaccountName", attrs));
                userInfo.put("email", nullCheck(attrs.get("mail"), "mail", attrs));
                userInfo.put("department", nullCheck(attrs.get("department"), "department", attrs));
            }
        } catch (NamingException | NullPointerException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    //carry, spm, sms 에서 쓰임
    @RequestMapping(method = RequestMethod.GET, value = "/ldap/search/badge/{pager}")
    public String[] ldap_search_by_badge(@PathVariable String pager) throws Exception {

        String site = "ATK";
        String userinfo[] = new String[4];
        String path = String.format(getLdapAddressBySite(site));
        String filterString = "pager=" + pager.trim();

        ret = "false";
        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();

                userinfo[0] = nullCheck(attrs.get("displayname"), "displayname", attrs);
                userinfo[1] = nullCheck(attrs.get("samaccountName"), "samaccountName", attrs);
                userinfo[2] = nullCheck(attrs.get("mail"), "mail", attrs);
                userinfo[3] = nullCheck(attrs.get("department"), "department", attrs);
            }
        } catch (NamingException | NullPointerException e) {
            e.printStackTrace();
        }
        return userinfo;
    }

    //carry 에서만 쓰임
    @RequestMapping(method = RequestMethod.GET, value = "/ldap/user_department/{site}/{userID}")
    public HashMap<String, String> user_search_department(
            @PathVariable String site, @PathVariable String userID) throws Exception {

        HashMap<String, String> userInfo = new HashMap<>();
        String path = String.format(getLdapAddressBySite(site));
        String filterString = "sAMAccountName=" + userID.trim();

        ret = "false";
        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                userInfo.put("username", nullCheck(attrs.get("displayname"), "displayname", attrs));
                userInfo.put("email", nullCheck(attrs.get("mail"), "mail", attrs));
                userInfo.put("department", nullCheck(attrs.get("department"), "department", attrs));
                userInfo.put("telephoneNumber", nullCheck(attrs.get("telephoneNumber"), "telephoneNumber", attrs));
                userInfo.put("company", nullCheck(attrs.get("company"), "company", attrs));
            }
        } catch (NamingException | NullPointerException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    //id 로 badge 검색
    @RequestMapping(method = RequestMethod.GET, value = "/ldap/user_badge/{site}/{userID}")
    public HashMap<String, String> user_search_badge(
            @PathVariable String site, @PathVariable String userID) throws Exception {

        HashMap<String, String> userInfo = new HashMap<>();
        String path = String.format(getLdapAddressBySite(site));
        String filterString = "sAMAccountName=" + userID.trim();

        ret = "false";
        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();
                userInfo.put("pager", nullCheck(attrs.get("pager"), "pager", attrs));
            }
        } catch (NamingException | NullPointerException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    // 사번으로 LDAP login -- bypass 에서 쓰임 (carry, wi)
    @RequestMapping(method = RequestMethod.GET, value = "/ldap/login/wtBadge/{pager}")
    public LoginModel Ldap_Login_by_badge(
            @PathVariable("pager") String pager) throws Exception {

        String site = "ATK";
        LoginModel userVO = new LoginModel();
        String path = String.format(getLdapAddressBySite(site));

        String filterString = "pager=" + pager;
        ret = "false";
        DirContext context = null;

        try {
            context = new InitialDirContext(getProperties(path));
            SearchControls searcher = new SearchControls();
            searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String ldapBasedn = getLdapBaseBySite(site);
            NamingEnumeration<SearchResult> results = context.search(ldapBasedn, filterString, searcher);

            if (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();

                userVO.setSite(site);
                userVO.setSAMAccountName(nullCheck(attrs.get("sAMAccountName"), "sAMAccountName", attrs));
                userVO.setDisplayname(nullCheck(attrs.get("displayname"), "displayname", attrs));
                userVO.setEmail(nullCheck(attrs.get("mail"), "mail", attrs));
                userVO.setOffice(
                        nullCheck(attrs.get("physicalDeliveryOfficeName"), "physicalDeliveryOfficeName", attrs));
                userVO.setDepartment(nullCheck(attrs.get("department"), "department", attrs));
                userVO.setPosition(nullCheck(attrs.get("title"), "title", attrs));

                if (site.equals("ATK") || site.equals("ATV")) {
                    userVO.setPager(nullCheck(attrs.get("pager"), "pager", attrs));
                } else {
                    String sa = nullCheck(attrs.get("sAMAccountName"), "sAMAccountName", attrs);
                    if (isNumeric(sa)) {
                        userVO.setPager(sa);
                    } else {
                        userVO.setPager("");
                    }
                }

                userVO.setSn(nullCheck(attrs.get("sn"), "sn", attrs));
                userVO.setTelephoneNumber(nullCheck(attrs.get("telephoneNumber"), "telephoneNumber", attrs));
                if (site.equals("ATK")) {
                    userVO.setPlant(nullCheck(attrs.get("company"), "company", attrs));
                } else {
                    userVO.setPlant(getPlantBySite(site));
                }

            }

        } catch (NamingException e) {
            System.out.println(userVO.toString());
            e.printStackTrace();
        }

        return userVO;
    }


}