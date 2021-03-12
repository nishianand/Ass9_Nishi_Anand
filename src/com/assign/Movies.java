package com.assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.assign.MovieEnum.Category;
import com.assign.MovieEnum.Language;
import com.assign.exceptions.IncorrectValueCountException;
import com.assign.exceptions.InvalidLanguageException;
import com.assign.exceptions.InvalidMovieIdException;


public class Movies {
	
	Connection movieDatabaseConnection;
	
	public Movies()
	{
		movieDatabaseConnection = DatabaseConnection.getConnection();
	}
	
	public List<Movie> populateMovies(File file) {
		List <Movie> movieList = new ArrayList<>();
		
		try (FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr)) {
			
			String line = "";
			Integer movieId;
			String movieName;
			Category movieType;
			Language language;
			Date releaseDate;
			List<String> casting;
			Double rating;
			Double totalBusinessDone;
			
			while ((line = br.readLine()) != null) {
				if (FileIsValid(line)) {
					String[] tokens = line.split(",");
					
					movieId = Integer.parseInt(tokens[0]);
					movieName = tokens[1];
					movieType = parseMovieType(tokens[2]);
					language = parseLanguage(tokens[3]);
					releaseDate = parseDate(tokens[4]);
					casting = parseCasting(tokens[5]);
					rating = Double.parseDouble(tokens[6]);
					totalBusinessDone = Double.parseDouble(tokens[7]);
					
					Movie curMovie = new Movie(movieId, movieName, movieType, language, releaseDate, casting, rating, totalBusinessDone);
					movieList.add(curMovie);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return movieList;
	}


	private boolean FileIsValid(String line) {
		boolean isValid = true;
		
		try {
			System.out.println(line);
			
			String[] tokens = line.split(",");
			
			if (tokens.length != 8) {
				throw new IncorrectValueCountException(tokens.length);
			}	
			
			try {
				Integer.parseInt(tokens[0]);
			} catch (NumberFormatException e) {
				throw new InvalidMovieIdException(tokens[0]);
			}
			
			if (! inLanguagesEnum(tokens[3])) {
				throw new InvalidLanguageException(tokens[3]);
			}
			
		} catch (IncorrectValueCountException | InvalidLanguageException | InvalidMovieIdException e) {
			isValid = false;
			System.out.println("^^^");
			e.printStackTrace();
		}
		
		return isValid;
	}
	

	private boolean inLanguagesEnum(String language) {
		boolean present = false;
		
		for (Language prod : Language.values()) {
			if (prod.getLanguage().equals(language)) {
				present = true;
				break;
			}
		}
		
		return present;
	}
	
	private Category parseMovieType(String movieType) {
		String movieTypeToCmp = movieType.toUpperCase();
		Category catMovieType = null;
		
		for (Category cat : Category.values()) {
			if (cat.name().equals(movieTypeToCmp)) {
				catMovieType = cat;
				break;
			}
		}
		
		return catMovieType;
	}
	
	private Language parseLanguage(String language) {
		Language languageToReturn = null;
		
		for (Language lang : Language.values()) {
			if (lang.getLanguage().equals(language) || lang.name().equals(language)) {
				languageToReturn = lang;
			}
		}
		
		return languageToReturn;
	}
	
	private Date parseDate(String strDate) {
		int dateIdx = 0, monthIdx = 1, yearIdx = 2;
		String formattedDate = "";
		Date parsedDate = null;
		
		String[] tokenDate = strDate.split("/");
		String date = tokenDate[dateIdx];
		String month = tokenDate[monthIdx];
		String year = tokenDate[yearIdx];
		
		date = date.length() == 1 ? "0" + date : date;
		month = month.length() == 1 ? "0" + month : month;
		
		formattedDate += "20" + year + "-" + month + "-" + date;
		parsedDate = Date.valueOf(formattedDate);
		
		return parsedDate;
	}
	
	private List<String> parseCasting(String casting) {
		List <String> castList = new ArrayList<> ();
		String[] castingTokens = casting.split(";");
		
		for (String token : castingTokens) {
			castList.add(token);
		}
		
		return castList;
	}
	

	public boolean addAllMoviesInDb(List <Movie> movies) {
		boolean successInAddingMovieInDb = true;
		
		String sql = "insert into movieDetails values (?,?,?,?,?,?,?,?)";
		int rowsInserted = 0;
		
		try {
			PreparedStatement prep = movieDatabaseConnection.prepareStatement(sql);
			
			for (Movie movie : movies) {
				
				try {
					prep.setInt(1, movie.getMovieId());
					prep.setString(2, movie.getMovieName());
					prep.setString(3, movie.getMovieType().name());
					prep.setString(4, movie.getLanguage().name());
					prep.setDate(5, movie.getReleaseDate());
					prep.setString(6, deParseCasting(movie.getCasting()));
					prep.setDouble(7, movie.getRating());
					prep.setDouble(8, movie.getTotalBusinessDone());
					
					rowsInserted += prep.executeUpdate();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}	
			}
			
			if (rowsInserted == 0) {
				throw new SQLException("0 rows inserted.");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			successInAddingMovieInDb = false;
		}

		System.out.println("Rows inserted in the database = " + rowsInserted);
		return successInAddingMovieInDb;
	}
	
	private String deParseCasting(List <String> casting) {
		String castStr = String.join(";", casting);
		return castStr;
	} 
	
	public void printMovieList(List <Movie> movies) {
		for (Movie movie : movies) {
			System.out.println(movie);
		}
		System.out.println();
	}
	
	public void displayHeading(String heading) {
		System.out.println(heading);
		
	}
	
	public void addMovie(Movie movie, List<Movie> movies) {
		System.out.println("Adding movie - " + movie.getMovieName());
		System.out.println(movie);
		
		movies.add(movie);
	}
	
	public Movie getDummyMovie() {
		Integer movieId = 11;
		String movieName = "Welcome";
		Category movieType = Category.DRAMA;
		Language language = Language.ENGLISH;
		Date releaseDate = Date.valueOf("2020-09-09");
		List<String> casting = new ArrayList<String>(Arrays.asList(new String[] {"Tom Hanks", "Laura Linney"}));
		Double rating = 3.4;
		Double totalBusinessDone = 240d;
		
		Movie movie = new Movie(movieId, movieName, movieType, language, releaseDate, casting, rating, totalBusinessDone);
		
		
		return movie;
	}
	
	public void serializeMovies(List <Movie> movies, String fileName) {
		File file = new File(fileName);
		
		try (FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream out = new ObjectOutputStream(fos)){
			
			out.writeObject(movies);
			
			System.out.println("Serialized movies list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Movie> deserializeMovies(String fileName) {
		File file = new File(fileName);
		List<Movie> movies = new ArrayList<>();
		
		try(FileInputStream fis = new FileInputStream(file);
				ObjectInputStream in = new ObjectInputStream(fis)) {
			
			movies = (ArrayList<Movie>) in.readObject();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return movies;
	}
	
	
	private List<Movie> convertResultSetToMovieList(ResultSet rs) {
		List <Movie> movies = new ArrayList<> ();
		
		Integer movieId;
		String movieName;
		Category movieType;
		Language language;
		Date releaseDate;
		List<String> casting;
		Double rating;
		Double totalBusinessDone;
		
		try {
			while(rs.next()) {
				movieId = rs.getInt(1);
				movieName = rs.getString(2);
				movieType = parseMovieType(rs.getString(3));
				language = parseLanguage(rs.getString(4));
				releaseDate = rs.getDate(5);
				casting = parseCasting(rs.getString(6));
				rating = rs.getDouble(7);
				totalBusinessDone = rs.getDouble(8);
				
				Movie movie = new Movie(movieId, movieName, movieType, language, releaseDate, casting, rating, totalBusinessDone);
				movies.add(movie);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return movies;
	}
	
	List <Movie> getMoviesReleasedInYear(int year) {
		List <Movie> movies = new ArrayList<>();
		String sql = "select * from movieDetails where to_char(releaseDate, 'yyyy') = ?";
		
		try {
			PreparedStatement prep = movieDatabaseConnection.prepareStatement(sql);
			
			prep.setInt(1, year);
			ResultSet rs = prep.executeQuery();
			
			movies = convertResultSetToMovieList(rs);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return movies;
	}
	
	
	
	public List<Movie> getMoviesByActor(String ... actorNames) {
		List <Movie> movies = new ArrayList<>();
		int actorNameCount = actorNames.length;
		
		if (actorNameCount > 0) {
			String sql = "select * from movieDetails where casting like ?";
			
			for (int i = 1; i < actorNameCount; i++) {
				sql += " or casting like ?";
			}
			
			try {
				PreparedStatement prep = movieDatabaseConnection.prepareStatement(sql);
				
				for(int i = 0; i < actorNameCount; i++) {
					prep.setString(i+1, "%" + actorNames[i] + "%");
				}
				
				ResultSet rs = prep.executeQuery();
				movies = convertResultSetToMovieList(rs);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return movies;
	}
	
	
	public void updateRatings(Movie movie, double rating, List<Movie> movies) {
		for(Movie s1 : movies) {
			if (movie.equals(s1)) {
				s1.setRating(rating);
			}
		}
	}
	
	public void updateBusiness(Movie movie, double amount, List<Movie> movies) {
		for (Movie s2 : movies) {
			if (movie.equals(s2)) {
				s2.setTotalBusinessDone(amount);
			}
		}
	}
	
	public Map<Language, LinkedHashSet<Movie>> businessDone (double amount) {
		Map <Language, LinkedHashSet<Movie>> langToMovie = new LinkedHashMap<> ();
		
		String sql = "select * from movieDetails where totalbusinessdone > ? order by totalbusinessdone desc";
		try {
			PreparedStatement prep = movieDatabaseConnection.prepareStatement(sql);
			prep.setDouble(1, amount);
			ResultSet rs = prep.executeQuery();
			
			List<Movie> movies = convertResultSetToMovieList(rs);
			
			for (Movie movie : movies) {
				Language curLang = movie.getLanguage();
				
				if (langToMovie.containsKey(curLang)) {
					langToMovie.get(curLang).add(movie);
				} else {
					LinkedHashSet<Movie> curSet = new LinkedHashSet<>();
					curSet.add(movie);
					langToMovie.put(movie.getLanguage(), curSet);
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return langToMovie;
	}
	
	public void displayLangToMovieMap(Map<Language, LinkedHashSet<Movie>> langToMovie) {
		for (Language lang : langToMovie.keySet()) {
			System.out.println(lang + " = ");
			for (Movie movie : langToMovie.get(lang)) {
				System.out.println(movie);
			}
			System.out.println("-");
		}
	}
	
	
	public static void main(String[] args) {
		Movies MovieObj = new Movies();
		File file = new File("movies.txt");
		List <Movie> myMovies = MovieObj.populateMovies(file);
		
		MovieObj.displayHeading("Populate movies list from file - " + file.getName());
		MovieObj.printMovieList(myMovies);
		
		MovieObj.displayHeading("Store all movies in db");
		System.out.println(MovieObj.addAllMoviesInDb(myMovies));
		
		
		MovieObj.displayHeading("Add new movie in the list");
		Movie dummyMovie = MovieObj.getDummyMovie();
		MovieObj.addMovie(dummyMovie, myMovies);
		System.out.println();
		MovieObj.printMovieList(myMovies);
		
		String serialFileName = "serialFileName";
		MovieObj.displayHeading("Serialize movie data to - " + serialFileName);
		MovieObj.serializeMovies(myMovies, serialFileName);
		
		MovieObj.displayHeading("Deserialize movie data from - " + serialFileName);
		List <Movie> deserialMovies = MovieObj.deserializeMovies(serialFileName);
		MovieObj.printMovieList(deserialMovies);
		
		int year = 2012;
		MovieObj.displayHeading("Find movies released in entered year - " + year);
		List <Movie> moviesByReleaseYear = MovieObj.getMoviesReleasedInYear(year);
		MovieObj.printMovieList(moviesByReleaseYear);
		
		String [] actors = new String [] {"Amir", "Kareena", "Tom"};
		MovieObj.displayHeading("List of movies by actors - " + String.join(" - ", actors));
		List<Movie> moviesByActor = MovieObj.getMoviesByActor(actors);
		MovieObj.printMovieList(moviesByActor);
		
		
		Double updatedRating = 4.4;
		MovieObj.displayHeading("Update rating of " + dummyMovie.getMovieName() + " to " + updatedRating);
		MovieObj.updateRatings(dummyMovie, updatedRating, myMovies);
		MovieObj.printMovieList(myMovies);
		
		Double updatedBusiness = 245.3;
		MovieObj.displayHeading("Updating total business done of " + dummyMovie.getMovieName() + " to " + updatedBusiness);
		MovieObj.updateBusiness(dummyMovie, updatedBusiness, myMovies);
		MovieObj.printMovieList(myMovies);
		
		Double businessThreshold = 24d;
		MovieObj.displayHeading("Display movies by language where business done is greater than - " + businessThreshold);
		Map<Language, LinkedHashSet<Movie>> langToMovie = MovieObj.businessDone(businessThreshold);
		MovieObj.displayLangToMovieMap(langToMovie);
		
	}
	
	
}
