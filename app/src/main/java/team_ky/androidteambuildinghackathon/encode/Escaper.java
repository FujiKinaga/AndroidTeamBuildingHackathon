package team_ky.androidteambuildinghackathon.encode;

/**
 * Created by kinagafuji on 15/11/22.
 */
public interface Escaper {
    public String escape(String string);

    public Appendable escape(Appendable out);
}
