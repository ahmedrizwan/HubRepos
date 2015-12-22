package minimize.com.hubrepos.realm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContributorStats {

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("author")
    @Expose
    private Author author;

    /**
     * @return The total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total The total
     */
    public void setTotal(int total) {
        this.total = total;
    }


    /**
     * @return The author
     */
    public Author getAuthor() {
        return author;
    }

    /**
     * @param author The author
     */
    public void setAuthor(Author author) {
        this.author = author;
    }

}
