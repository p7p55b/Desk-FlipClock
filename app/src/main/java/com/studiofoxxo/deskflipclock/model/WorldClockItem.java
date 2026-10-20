package com.studiofoxxo.deskflipclock.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public final class WorldClockItem {

    private static List<WorldClockItem> ctlgcch;

    public final String ct;
    public final String rgn;
    public final String tzd;
    public final String srch;
    public final TimeZone tz;

    public WorldClockItem(String ct, String rgn, String tzd) {
        this.ct = ct;
        this.rgn = rgn != null ? rgn : "";
        this.tzd = tzd;
        this.srch = (this.ct + " " + this.rgn + " " + this.tzd).toLowerCase(Locale.FRANCE);
        this.tz = TimeZone.getTimeZone(tzd);
    }

    public String fmtffst(long nw, TimeZone lcltz, Calendar clcl, Calendar ctrgt) {
        int dfm = (tz.getOffset(nw) - lcltz.getOffset(nw)) / 60000;
        String fst;
        if (dfm == 0) {
            fst = "0h";
        } else {
            int bs = Math.abs(dfm);
            int h = bs / 60;
            int m = bs % 60;
            String sgn = dfm > 0 ? "+" : "-";
            fst = m == 0 ? sgn + h + "h" : String.format(Locale.US, "%s%dh%02d", sgn, h, m);
        }

        clcl.setTimeZone(lcltz);
        clcl.setTimeInMillis(nw);
        ctrgt.setTimeZone(tz);
        ctrgt.setTimeInMillis(nw);

        int lrd = clcl.get(Calendar.YEAR) * 400 + clcl.get(Calendar.DAY_OF_YEAR);
        int trd = ctrgt.get(Calendar.YEAR) * 400 + ctrgt.get(Calendar.DAY_OF_YEAR);

        if (trd > lrd) return "[ " + fst + " / J+1 ]";
        if (trd < lrd) return "[ " + fst + " / J-1 ]";
        return "[ " + fst + " ]";
    }

    public static List<WorldClockItem> gtctlg() {
        if (ctlgcch != null) return ctlgcch;

        String[][] prsts = {
                {"Guangzhou", "Guangdong, Chine", "Asia/Shanghai"},
                {"Shenzhen", "Guangdong, Chine", "Asia/Shanghai"},
                {"Dongguan", "Guangdong, Chine", "Asia/Shanghai"},
                {"Foshan", "Guangdong, Chine", "Asia/Shanghai"},
                {"Zhuhai", "Guangdong, Chine", "Asia/Shanghai"},
                {"Beijing", "Chine", "Asia/Shanghai"},
                {"Shanghai", "Chine", "Asia/Shanghai"},
                {"Chengdu", "Sichuan, Chine", "Asia/Shanghai"},
                {"Chongqing", "Chine", "Asia/Shanghai"},
                {"Wuhan", "Hubei, Chine", "Asia/Shanghai"},
                {"Hangzhou", "Zhejiang, Chine", "Asia/Shanghai"},
                {"Nanjing", "Jiangsu, Chine", "Asia/Shanghai"},
                {"Xi'an", "Shaanxi, Chine", "Asia/Shanghai"},
                {"Suzhou", "Jiangsu, Chine", "Asia/Shanghai"},
                {"Tianjin", "Chine", "Asia/Shanghai"},
                {"Hong Kong", "Chine", "Asia/Hong_Kong"},
                {"Macau", "Chine", "Asia/Macau"},
                {"Taipei", "Taïwan", "Asia/Taipei"},
                {"Tokyo", "Japon", "Asia/Tokyo"},
                {"Osaka", "Japon", "Asia/Tokyo"},
                {"Kyoto", "Japon", "Asia/Tokyo"},
                {"Yokohama", "Japon", "Asia/Tokyo"},
                {"Nagoya", "Japon", "Asia/Tokyo"},
                {"Sapporo", "Japon", "Asia/Tokyo"},
                {"Fukuoka", "Japon", "Asia/Tokyo"},
                {"Séoul", "Corée du Sud", "Asia/Seoul"},
                {"Busan", "Corée du Sud", "Asia/Seoul"},
                {"Singapour", "Singapour", "Asia/Singapore"},
                {"Kuala Lumpur", "Malaisie", "Asia/Kuala_Lumpur"},
                {"Bangkok", "Thaïlande", "Asia/Bangkok"},
                {"Hanoï", "Vietnam", "Asia/Ho_Chi_Minh"},
                {"Hô Chi Minh-Ville", "Vietnam", "Asia/Ho_Chi_Minh"},
                {"Jakarta", "Indonésie", "Asia/Jakarta"},
                {"Bali", "Indonésie", "Asia/Makassar"},
                {"Manille", "Philippines", "Asia/Manila"},
                {"Mumbai", "Inde", "Asia/Kolkata"},
                {"New Delhi", "Inde", "Asia/Kolkata"},
                {"Bangalore", "Inde", "Asia/Kolkata"},
                {"Dubaï", "Émirats arabes unis", "Asia/Dubai"},
                {"Abou Dabi", "Émirats arabes unis", "Asia/Dubai"},
                {"Doha", "Qatar", "Asia/Qatar"},
                {"Riyad", "Arabie saoudite", "Asia/Riyadh"},
                {"Sydney", "Australie", "Australia/Sydney"},
                {"Melbourne", "Australie", "Australia/Melbourne"},
                {"Perth", "Australie", "Australia/Perth"},
                {"Brisbane", "Australie", "Australia/Brisbane"},
                {"Auckland", "Nouvelle-Zélande", "Pacific/Auckland"},
                {"New York", "États-Unis", "America/New_York"},
                {"Miami", "Floride, É.-U.", "America/New_York"},
                {"Boston", "Massachusetts, É.-U.", "America/New_York"},
                {"Washington", "D.C., É.-U.", "America/New_York"},
                {"Atlanta", "Géorgie, É.-U.", "America/New_York"},
                {"Chicago", "Illinois, É.-U.", "America/Chicago"},
                {"Houston", "Texas, É.-U.", "America/Chicago"},
                {"Dallas", "Texas, É.-U.", "America/Chicago"},
                {"Denver", "Colorado, É.-U.", "America/Denver"},
                {"Los Angeles", "Californie, É.-U.", "America/Los_Angeles"},
                {"San Francisco", "Californie, É.-U.", "America/Los_Angeles"},
                {"Seattle", "Washington, É.-U.", "America/Los_Angeles"},
                {"Las Vegas", "Nevada, É.-U.", "America/Los_Angeles"},
                {"Honolulu", "Hawaï", "Pacific/Honolulu"},
                {"Montréal", "Québec, Canada", "America/Toronto"},
                {"Toronto", "Ontario, Canada", "America/Toronto"},
                {"Ottawa", "Ontario, Canada", "America/Toronto"},
                {"Vancouver", "C.-B., Canada", "America/Vancouver"},
                {"Mexico", "Mexique", "America/Mexico_City"},
                {"São Paulo", "Brésil", "America/Sao_Paulo"},
                {"Rio de Janeiro", "Brésil", "America/Sao_Paulo"},
                {"Buenos Aires", "Argentine", "America/Argentina/Buenos_Aires"},
                {"Santiago", "Chili", "America/Santiago"},
                {"Bogota", "Colombie", "America/Bogota"},
                {"Lima", "Pérou", "America/Lima"},
                {"Londres", "Royaume-Uni", "Europe/London"},
                {"Paris", "France", "Europe/Paris"},
                {"Lyon", "France", "Europe/Paris"},
                {"Marseille", "France", "Europe/Paris"},
                {"Berlin", "Allemagne", "Europe/Berlin"},
                {"Francfort", "Allemagne", "Europe/Berlin"},
                {"Munich", "Allemagne", "Europe/Berlin"},
                {"Genève", "Suisse", "Europe/Zurich"},
                {"Zurich", "Suisse", "Europe/Zurich"},
                {"Bruxelles", "Belgique", "Europe/Brussels"},
                {"Amsterdam", "Pays-Bas", "Europe/Amsterdam"},
                {"Madrid", "Espagne", "Europe/Madrid"},
                {"Barcelone", "Espagne", "Europe/Madrid"},
                {"Lisbonne", "Portugal", "Europe/Lisbon"},
                {"Rome", "Italie", "Europe/Rome"},
                {"Milan", "Italie", "Europe/Rome"},
                {"Vienne", "Autriche", "Europe/Vienna"},
                {"Prague", "Tchéquie", "Europe/Prague"},
                {"Varsovie", "Pologne", "Europe/Warsaw"},
                {"Stockholm", "Suède", "Europe/Stockholm"},
                {"Oslo", "Norvège", "Europe/Oslo"},
                {"Copenhague", "Danemark", "Europe/Copenhagen"},
                {"Helsinki", "Finlande", "Europe/Helsinki"},
                {"Athènes", "Grèce", "Europe/Athens"},
                {"Istanbul", "Turquie", "Europe/Istanbul"},
                {"Moscou", "Russie", "Europe/Moscow"},
                {"Reykjavik", "Islande", "Atlantic/Reykjavik"},
                {"Le Caire", "Égypte", "Africa/Cairo"},
                {"Casablanca", "Maroc", "Africa/Casablanca"},
                {"Alger", "Algérie", "Africa/Algiers"},
                {"Tunis", "Tunisie", "Africa/Tunis"},
                {"Dakar", "Sénégal", "Africa/Dakar"},
                {"Abidjan", "Côte d'Ivoire", "Africa/Abidjan"},
                {"Lagos", "Nigeria", "Africa/Lagos"},
                {"Nairobi", "Kenya", "Africa/Nairobi"},
                {"Johannesburg", "Afrique du Sud", "Africa/Johannesburg"},
                {"La Réunion", "Outre-Mer", "Indian/Reunion"},
                {"Mayotte", "Outre-Mer", "Indian/Mayotte"},
                {"Nouméa", "Nouvelle-Calédonie", "Pacific/Noumea"},
                {"Papeete", "Tahiti, Polynésie", "Pacific/Tahiti"},
                {"Fort-de-France", "Martinique", "America/Martinique"},
                {"Pointe-à-Pitre", "Guadeloupe", "America/Guadeloupe"},
                {"Cayenne", "Guyane", "America/Cayenne"}
        };

        List<WorldClockItem> lst = new ArrayList<>(prsts.length + 450);
        Set<String> snct = new HashSet<>();
        for (String[] p : prsts) {
            lst.add(new WorldClockItem(p[0], p[1], p[2]));
            snct.add(p[0].toLowerCase(Locale.ROOT));
        }

        for (String d : TimeZone.getAvailableIDs()) {
            if (!d.contains("/") || d.startsWith("Etc/") || d.startsWith("SystemV/")) continue;
            int slsh = d.lastIndexOf('/');
            String rgn = d.substring(0, d.indexOf('/')).replace('_', ' ');
            String ct = d.substring(slsh + 1).replace('_', ' ');
            if (snct.add(ct.toLowerCase(Locale.ROOT))) {
                lst.add(new WorldClockItem(ct, rgn, d));
            }
        }

        Collections.sort(lst, (x, z) -> x.ct.compareToIgnoreCase(z.ct));
        ctlgcch = Collections.unmodifiableList(lst);
        return ctlgcch;
    }
}
