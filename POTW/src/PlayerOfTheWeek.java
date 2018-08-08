import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerOfTheWeek {

	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://coloradocricket.org:3306/colorad2_db1",
					"colorad2_rouser", "f)R_HWjrwdIW");
			Statement stmt = con.createStatement();
			System.out.println("[b]Honorable mentions[/b]\n");
			ResultSet rs = stmt.executeQuery(
					"SELECT g.game_id AS game_id, ht.TeamAbbrev AS HomeTeam, at.TeamAbbrev AS AwayTeam, g.mom AS mom FROM scorecard_game_details g, "
							+ "teams ht, teams at WHERE g.game_date > CURDATE() - 8 AND g.league_id = 1 AND g.hometeam = ht.TeamID AND "
							+ "g.awayteam = at.TeamID");
			if (rs.next()) {
				System.out.println("[b]Premier[/b]\n");
				do {
					System.out.println("[b]" + rs.getString("HomeTeam") + " vs " + rs.getString("AwayTeam") + "[/b]");
					Statement stmt1 = con.createStatement();
					ResultSet rs1 = stmt1.executeQuery(
							"SELECT PlayerFName, PlayerLName from players where PlayerID = " + rs.getInt("mom"));
					if (rs1.next()) {
						System.out.print("[b]Man of the Match: " + rs1.getString("PlayerFName") + " " + rs1.getString("PlayerLName") + "[/b] - ");
					}
					ResultSet rs2 = stmt1.executeQuery(
							"SELECT runs, balls, sixes, fours from scorecard_batting_details where game_id = " + rs.getInt("game_id") + " and player_id = " + rs.getInt("mom") + " and runs > 20");
					String and = "";
					if (rs2.next()) {
						System.out.print(rs2.getInt("runs") + " of " + rs2.getInt("balls") + " balls (" + rs2.getInt("fours") + "x4 and " + rs2.getInt("sixes") + "x6)");
						and = " and ";
					}
					ResultSet rs3 = stmt1.executeQuery(
							"SELECT overs, maidens, runs, wickets from scorecard_bowling_details where game_id = " + rs.getInt("game_id") + " and player_id = " + rs.getInt("mom") + " and wickets > 0");
					if (rs3.next()) {
						DecimalFormat df = new DecimalFormat("#.#");
						System.out.print(and + df.format(rs3.getDouble("overs")) + "-" + rs3.getInt("maidens") + "-" + rs3.getInt("runs") + "-" + rs3.getInt("wickets"));
					}
					System.out.println("");
					List<String> playerIds = new ArrayList<>();
					ResultSet rs4 = stmt1.executeQuery(
							"SELECT bt.player_id AS player_id, pl.PlayerFName AS PlayerFName, pl.PlayerLName AS PlayerLName, bt.runs AS runs, bt.balls AS balls, bt.sixes AS sixes, "
							+ "bt.fours AS fours, bl.overs as overs, bl.maidens AS maidens, bl.runs AS blruns, bl.wickets AS wickets from scorecard_batting_details bt "
							+ "LEFT JOIN scorecard_bowling_details bl ON bl.player_id = bt.player_id AND bt.game_id = bl.game_id AND bl.wickets > 0 "
							+ "INNER JOIN players pl ON pl.PlayerID = bt.player_id where bt.game_id = " + rs.getInt("game_id") + " and bt.player_id != " + rs.getInt("mom") + " and bt.runs >= 30");
					while (rs4.next()) {
						playerIds.add(rs4.getInt("player_id") + "");
						DecimalFormat df = new DecimalFormat("#.#");
						System.out.print(rs4.getString("PlayerFName") + " " + rs4.getString("PlayerLName") + " - ");
						and = "";
						if(rs4.getInt("runs") != 0) {
							System.out.print(rs4.getInt("runs") + " of " + rs4.getInt("balls") + " balls (" + rs4.getInt("fours") + "x4 and " + rs4.getInt("sixes") + "x6)");
							and = " and ";
						}
						if(rs4.getInt("wickets") != 0) {
							System.out.print(and + df.format(rs4.getDouble("overs")) + "-" + rs4.getInt("maidens") + "-" + rs4.getInt("blruns") + "-" + rs4.getInt("wickets"));
						}
						System.out.println("");
					}
					ResultSet rs5 = stmt1.executeQuery(
							"SELECT pl.PlayerFName AS PlayerFName, pl.PlayerLName AS PlayerLName, bt.runs AS runs, bt.balls AS balls, bt.sixes AS sixes, "
							+ "bt.fours AS fours, bl.overs as overs, bl.maidens AS maidens, bl.runs AS blruns, bl.wickets AS wickets from "
							+ "scorecard_bowling_details bl LEFT JOIN scorecard_batting_details bt ON bl.player_id = bt.player_id AND bt.game_id = bl.game_id "
							+ "AND bt.runs > 10 INNER JOIN players pl ON pl.PlayerID = bl.player_id where bl.game_id = " + rs.getInt("game_id") 
							+ " and bl.player_id != " + rs.getInt("mom") + " and bl.player_id NOT IN ('" + String.join("','", playerIds) + "') and bl.wickets >= 3");
					while (rs5.next()) {
						DecimalFormat df = new DecimalFormat("#.#");
						System.out.print(rs5.getString("PlayerFName") + " " + rs5.getString("PlayerLName") + " - ");
						and = "";
						if(rs5.getInt("runs") != 0) {
							System.out.print(rs5.getInt("runs") + " of " + rs5.getInt("balls") + " balls (" + rs5.getInt("fours") + "x4 and " + rs5.getInt("sixes") + "x6)");
							and = " and ";
						}
						if(rs5.getInt("wickets") != 0) {
							System.out.print(and + df.format(rs5.getDouble("overs")) + "-" + rs5.getInt("maidens") + "-" + rs5.getInt("blruns") + "-" + rs5.getInt("wickets"));
						}
						System.out.println("");
					}
					System.out.println("");
					System.out.println("See CCL's [link=http://coloradocricket.org/scorecardfull.php?game_id=" + rs.getInt("game_id") + "&ccl_mode=4]" + rs.getString("HomeTeam") + " vs " + rs.getString("AwayTeam") + " scorecard[/link]\n");
					stmt1.close();
					rs1.close();
				} while (rs.next());
			}
			rs.close();
			rs = stmt.executeQuery(
					"SELECT g.game_id AS game_id, ht.TeamAbbrev AS HomeTeam, at.TeamAbbrev AS AwayTeam, g.mom AS mom FROM scorecard_game_details g, "
							+ "teams ht, teams at WHERE g.game_date > CURDATE() - 8 AND g.league_id = 4 AND g.hometeam = ht.TeamID AND "
							+ "g.awayteam = at.TeamID");
			if (rs.next()) {
				System.out.println("[b]T20[/b]\n");
				do {
					System.out.println("[b]" + rs.getString("HomeTeam") + " vs " + rs.getString("AwayTeam") + "[/b]");
					Statement stmt1 = con.createStatement();
					ResultSet rs1 = stmt1.executeQuery(
							"SELECT PlayerFName, PlayerLName from players where PlayerID = " + rs.getInt("mom"));
					if (rs1.next()) {
						System.out.print("[b]Man of the Match: " + rs1.getString("PlayerFName") + " " + rs1.getString("PlayerLName") + "[/b] - ");
					}
					ResultSet rs2 = stmt1.executeQuery(
							"SELECT runs, balls, sixes, fours from scorecard_batting_details where game_id = " + rs.getInt("game_id") + " and player_id = " + rs.getInt("mom") + " and runs > 20");
					String and = "";
					if (rs2.next()) {
						System.out.print(rs2.getInt("runs") + " of " + rs2.getInt("balls") + " balls (" + rs2.getInt("fours") + "x4 and " + rs2.getInt("sixes") + "x6)");
						and = " and ";
					}
					ResultSet rs3 = stmt1.executeQuery(
							"SELECT overs, maidens, runs, wickets from scorecard_bowling_details where game_id = " + rs.getInt("game_id") + " and player_id = " + rs.getInt("mom") + " and wickets > 0");
					if (rs3.next()) {
						DecimalFormat df = new DecimalFormat("#.#");
						System.out.print(and + df.format(rs3.getDouble("overs")) + "-" + rs3.getInt("maidens") + "-" + rs3.getInt("runs") + "-" + rs3.getInt("wickets"));
					}
					System.out.println("");
					List<String> playerIds = new ArrayList<>();
					ResultSet rs4 = stmt1.executeQuery(
							"SELECT bt.player_id AS player_id, pl.PlayerFName AS PlayerFName, pl.PlayerLName AS PlayerLName, bt.runs AS runs, bt.balls AS balls, bt.sixes AS sixes, "
							+ "bt.fours AS fours, bl.overs as overs, bl.maidens AS maidens, bl.runs AS blruns, bl.wickets AS wickets from scorecard_batting_details bt "
							+ "LEFT JOIN scorecard_bowling_details bl ON bl.player_id = bt.player_id AND bt.game_id = bl.game_id AND bl.wickets > 0 "
							+ "INNER JOIN players pl ON pl.PlayerID = bt.player_id where bt.game_id = " + rs.getInt("game_id") + " and bt.player_id != " + rs.getInt("mom") + " and bt.runs >= 30");
					while (rs4.next()) {
						playerIds.add(rs4.getInt("player_id") + "");
						DecimalFormat df = new DecimalFormat("#.#");
						System.out.print(rs4.getString("PlayerFName") + " " + rs4.getString("PlayerLName") + " - ");
						and = "";
						if(rs4.getInt("runs") != 0) {
							System.out.print(rs4.getInt("runs") + " of " + rs4.getInt("balls") + " balls (" + rs4.getInt("fours") + "x4 and " + rs4.getInt("sixes") + "x6)");
							and = " and ";
						}
						if(rs4.getInt("wickets") != 0) {
							System.out.print(and + df.format(rs4.getDouble("overs")) + "-" + rs4.getInt("maidens") + "-" + rs4.getInt("blruns") + "-" + rs4.getInt("wickets"));
						}
						System.out.println("");
					}
					ResultSet rs5 = stmt1.executeQuery(
							"SELECT pl.PlayerFName AS PlayerFName, pl.PlayerLName AS PlayerLName, bt.runs AS runs, bt.balls AS balls, bt.sixes AS sixes, "
							+ "bt.fours AS fours, bl.overs as overs, bl.maidens AS maidens, bl.runs AS blruns, bl.wickets AS wickets from "
							+ "scorecard_bowling_details bl LEFT JOIN scorecard_batting_details bt ON bl.player_id = bt.player_id AND bt.game_id = bl.game_id "
							+ "AND bt.runs > 10 INNER JOIN players pl ON pl.PlayerID = bl.player_id where bl.game_id = " + rs.getInt("game_id") 
							+ " and bl.player_id != " + rs.getInt("mom") + " and bl.player_id NOT IN ('" + String.join("','", playerIds) + "') and bl.wickets >= 3");
					while (rs5.next()) {
						DecimalFormat df = new DecimalFormat("#.#");
						System.out.print(rs5.getString("PlayerFName") + " " + rs5.getString("PlayerLName") + " - ");
						and = "";
						if(rs5.getInt("runs") != 0) {
							System.out.print(rs5.getInt("runs") + " of " + rs5.getInt("balls") + " balls (" + rs5.getInt("fours") + "x4 and " + rs5.getInt("sixes") + "x6)");
							and = " and ";
						}
						if(rs5.getInt("wickets") != 0) {
							System.out.print(and + df.format(rs5.getDouble("overs")) + "-" + rs5.getInt("maidens") + "-" + rs5.getInt("blruns") + "-" + rs5.getInt("wickets"));
						}
						System.out.println("");
					}
					System.out.println("");
					System.out.println("See CCL's [link=http://coloradocricket.org/scorecardfull.php?game_id=" + rs.getInt("game_id") + "&ccl_mode=4]" + rs.getString("HomeTeam") + " vs " + rs.getString("AwayTeam") + " scorecard[/link]\n");
					stmt1.close();
					rs1.close();
				} while (rs.next());
			}
			con.close();
			stmt.close();
			rs.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
