package evans.dave.androtag.common;

public interface Scoring {


	int getKills();
	int getDeaths();
	int getAssists();
	int getScore();

    boolean isDead();
	
	String getName();
	int getColor();

}
