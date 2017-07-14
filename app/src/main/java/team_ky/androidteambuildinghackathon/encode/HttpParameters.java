package team_ky.androidteambuildinghackathon.encode;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import team_ky.androidteambuildinghackathon.ShareToTwitter;

/**
 * Created by kinagafuji on 15/11/22.
 */
public class HttpParameters implements Map<String, SortedSet<String>>, Serializable {
    private TreeMap<String, SortedSet<String>> wrappedMap = new TreeMap<String, SortedSet<String>>();

    public SortedSet<String> put(String key, SortedSet<String> value) {
        return wrappedMap.put(key, value);
    }

    public SortedSet<String> put(String key, SortedSet<String> values, boolean percentEncode) {
        if (percentEncode) {
            remove(key);
            for (String v : values) {
                put(key, v, true);
            }
            return get(key);
        } else {
            return wrappedMap.put(key, values);
        }
    }

    public String put(String key, String value) {
        return put(key, value, false);
    }

    public String put(String key, String value, boolean percentEncode) {
        SortedSet<String> values = wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet<String>();
            wrappedMap.put(percentEncode ? ShareToTwitter.percentEncode(key) : key, values);
        }
        if (value != null) {
            value = percentEncode ? ShareToTwitter.percentEncode(value) : value;
            values.add(value);
        }

        return value;
    }

    public String putNull(String key, String nullString) {
        return put(key, nullString);
    }

    public void putAll(Map<? extends String, ? extends SortedSet<String>> m) {
        wrappedMap.putAll(m);
    }

    public void putAll(Map<? extends String, ? extends SortedSet<String>> m, boolean percentEncode) {
        if (percentEncode) {
            for (String key : m.keySet()) {
                put(key, m.get(key), true);
            }
        } else {
            wrappedMap.putAll(m);
        }
    }

    public void putAll(String[] keyValuePairs, boolean percentEncode) {
        for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
            this.put(keyValuePairs[i], keyValuePairs[i + 1], percentEncode);
        }
    }

    /**
     * Convenience method to merge a Map<String, List<String>>.
     *
     * @param m the map
     */
    public void putMap(Map<String, List<String>> m) {
        for (String key : m.keySet()) {
            SortedSet<String> vals = get(key);
            if (vals == null) {
                vals = new TreeSet<String>();
                put(key, vals);
            }
            vals.addAll(m.get(key));
        }
    }

    public SortedSet<String> get(Object key) {
        return wrappedMap.get(key);
    }

    public String getFirst(Object key) {
        return getFirst(key, false);
    }

    public String getFirst(Object key, boolean percentDecode) {
        SortedSet<String> values = wrappedMap.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        String value = values.first();
        return percentDecode ? ShareToTwitter.percentDecode(value) : value;
    }

    public String getAsQueryString(Object key) {
        StringBuilder sb = new StringBuilder();
        key = ShareToTwitter.percentEncode((String) key);
        Set<String> values = wrappedMap.get(key);
        if (values == null) {
            return key + "=";
        }
        Iterator<String> iter = values.iterator();
        while (iter.hasNext()) {
            sb.append(key + "=" + iter.next());
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public String getAsHeaderElement(String key) {
        String value = getFirst(key);
        if (value == null) {
            return null;
        }
        return key + "=\"" + value + "\"";
    }

    public boolean containsKey(Object key) {
        return wrappedMap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        for (Set<String> values : wrappedMap.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        int count = 0;
        for (String key : wrappedMap.keySet()) {
            count += wrappedMap.get(key).size();
        }
        return count;
    }

    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    public void clear() {
        wrappedMap.clear();
    }

    public SortedSet<String> remove(Object key) {
        return wrappedMap.remove(key);
    }

    public Set<String> keySet() {
        return wrappedMap.keySet();
    }

    public Collection<SortedSet<String>> values() {
        return wrappedMap.values();
    }

    public Set<java.util.Map.Entry<String, SortedSet<String>>> entrySet() {
        return wrappedMap.entrySet();
    }
}
