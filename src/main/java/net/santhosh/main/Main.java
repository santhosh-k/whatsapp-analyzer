package net.santhosh.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import emoji4j.EmojiUtils;
import net.santhosh.entities.Conversation;

/**
 * The Class Main.
 *
 * @author Sandy
 */
public class Main {

	/** The session factory. */
	private static SessionFactory sessionFactory;

	/** The word count. */
	private static Map<String, Integer> wordCount = new HashMap<>();

	/**
	 * Setup connection.
	 */
	protected static void setupConnection() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 */
	public static void main(String[] args) throws IOException, ParseException {
		setupConnection();
		String text = readFile(args[1]);
		Matcher matcher = getMatcher(text);
		List<String> wordsToSkip = getIgnoredWords();
		while (matcher.find()) {
			Conversation conversation = createConversation(matcher);
			addToDB(conversation);
			updateWordCount(wordCount, wordsToSkip, conversation.getMessage());
		}
		List<Entry<String, Integer>> entryList = sortWordCount(wordCount);
		for (Entry<String, Integer> entry : entryList) {
			System.out.println(entry.getKey()+" : "+entry.getValue());
		}
	}

	/**
	 * Sort word count.
	 *
	 * @param wordCount
	 *            the word count
	 * @return the list
	 */
	private static List<Entry<String, Integer>> sortWordCount(Map<String, Integer> wordCount) {
		Set<Entry<String, Integer>> set = wordCount.entrySet();
		List<Entry<String, Integer>> entryList = new ArrayList<>(set);
		Collections.sort(entryList,(o1,o2)-> o2.getValue().compareTo(o1.getValue()));
		entryList = entryList.subList(0, 50);
		return entryList;
	}

	/**
	 * Creates the conversation.
	 *
	 * @param matcher
	 *            the matcher
	 * @return the conversation
	 * @throws ParseException
	 *             the parse exception
	 */
	private static Conversation createConversation(Matcher matcher) throws ParseException {
		Conversation conversation = new Conversation();
		String conversationText = matcher.group();
		int indexOfComma = parseDate(conversation, conversationText);
		int indexOfHypen = parseTime(conversation, conversationText, indexOfComma);
		int indexOfColon = conversationText.indexOf(':', indexOfHypen);
		int indexOfMessage = parseSender(conversation, conversationText, indexOfHypen, indexOfColon);
		parseMessage(conversation, conversationText, indexOfMessage);
		return conversation;
	}

	private static void parseMessage(Conversation conversation, String conversationText, int indexOfMessage) {
		String message = conversationText.substring(indexOfMessage + 2);
		boolean containsSmiley = EmojiUtils.countEmojis(message) > 0;
		message = EmojiUtils.htmlify(message);
		conversation.setContainsSmiley(containsSmiley);
		conversation.setMessage(message);
	}

	private static int parseSender(Conversation conversation, String conversationText, int indexOfHypen,
			int indexOfColon) {
		int indexOfMessage = indexOfColon;
		if (indexOfColon > indexOfHypen) {
			String sender = conversationText.substring(indexOfHypen + 2, indexOfColon);
			conversation.setSender(sender);
		} else {
			indexOfMessage = indexOfHypen;
		}
		return indexOfMessage;
	}

	private static int parseTime(Conversation conversation, String conversationText, int indexOfComma) {
		int indexOfHypen = conversationText.indexOf('-', indexOfComma);
		String time = conversationText.substring(indexOfComma + 2, indexOfHypen);
		conversation.setTime_(time);
		return indexOfHypen;
	}

	private static int parseDate(Conversation conversation, String conversationText) throws ParseException {
		int indexOfComma = conversationText.indexOf(',', 0);
		String date = conversationText.substring(0, indexOfComma);
		conversation.setDate_(date);
		return indexOfComma;
	}

	/**
	 * Adds the to DB.
	 *
	 * @param conversation
	 *            the conversation
	 */
	private static void addToDB(Conversation conversation) {
		Session session = getSession();
		session.beginTransaction();
		session.save(conversation);
		session.getTransaction().commit();
	}

	/**
	 * Update word count.
	 *
	 * @param wordCount
	 *            the word count
	 * @param wordsToSkip
	 *            the words to skip
	 * @param message
	 *            the message
	 */
	private static void updateWordCount(Map<String, Integer> wordCount, List<String> wordsToSkip, String message) {
		String[] words = message.split(" ");
		for (String word : words) {
			word = word.trim();
			if (word.isEmpty() || wordsToSkip.contains(word))
				continue;
			Integer count = wordCount.get(word);
			if (count == null) {
				count = 1;
			} else {
				count++;
			}
			wordCount.put(word, count);
		}
	}

	/**
	 * Gets the matcher.
	 *
	 * @param text
	 *            the text
	 * @return the matcher
	 */
	private static Matcher getMatcher(String text) {
		Pattern pattern = Pattern.compile("([0-9]?[0-9]/[0-3]?[0-9]/[0-9][0-9]).*", Pattern.MULTILINE);
		return pattern.matcher(text);
	}

	/**
	 * Gets the ignored words.
	 *
	 * @return the ignored words
	 */
	private static List<String> getIgnoredWords() {
		List<String> wordsToSkip = new ArrayList<>();
		wordsToSkip.add("so");
		wordsToSkip.add("to");
		wordsToSkip.add("of");
		wordsToSkip.add("a");
		wordsToSkip.add("an");
		wordsToSkip.add("in");
		wordsToSkip.add("is");
		wordsToSkip.add("for");
		wordsToSkip.add("on");
		wordsToSkip.add("be");
		wordsToSkip.add("that");
		wordsToSkip.add("it");
		wordsToSkip.add("are");
		wordsToSkip.add("and");
		wordsToSkip.add("was");
		wordsToSkip.add("can");
		wordsToSkip.add("with");
		wordsToSkip.add("the");
		wordsToSkip.add("<Media");
		wordsToSkip.add("omitted>");
		return wordsToSkip;
	}

	/**
	 * Read file.
	 *
	 * @param path
	 *            the path
	 * @return the string
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static String readFile(String path) throws IOException {
		try (FileReader fileReader = new FileReader(new File(path)); FileInputStream fis = new FileInputStream(path)) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line+"\n");
			}
			bufferedReader.close();
			fileReader.close();
			return sb.toString();
		}
	}

	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	private static Session getSession() {
		return sessionFactory.openSession();
	}

}
