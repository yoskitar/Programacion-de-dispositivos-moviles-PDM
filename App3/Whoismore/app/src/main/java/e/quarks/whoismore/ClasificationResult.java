package e.quarks.whoismore;

public class ClasificationResult {
    String idUser;
    Integer votes;

    public ClasificationResult(){}

    public ClasificationResult(String id, Integer cnt){
        this.idUser = id;
        this.votes = cnt;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}
