/**
 * 
 */
package net.santhosh.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emoji4j.EmojiUtils;
import net.santhosh.entities.Conversation;

/**
 * @author Sandy
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String path = args[1];
		System.out.println(path);
		FileReader fileReader = new FileReader(new File(path));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		String line = null;
		int i = 0;
		StringBuilder sb = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
			if (i == 200) {
				break;
			}
			i++;
		}
		bufferedReader.close();
		fileReader.close();
		String text = sb.toString();
		Pattern pattern = Pattern.compile("([0-9]?[0-9]/[0-3]?[0-9]/[0-9][0-9]).*", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(text);
		List<Conversation> conversations = new ArrayList<>();
		Map<String, Integer> wordCount = new HashMap<>();
		while (matcher.find()) {
			Conversation conversation = new Conversation();
			String conversationText = matcher.group();
			int indexOfComma = conversationText.indexOf(",", 0);
			String date = conversationText.substring(0, indexOfComma);
			conversation.setDate(date);
			int indexOfHypen = conversationText.indexOf("-", indexOfComma);
			String time = conversationText.substring(indexOfComma + 2, indexOfHypen);
			conversation.setTime(time);
			int indexOfColon = conversationText.indexOf(":", indexOfHypen);
			int indexOfMessage = indexOfColon;
			if (indexOfColon > indexOfHypen) {
				String sender = conversationText.substring(indexOfHypen + 2, indexOfColon);
				conversation.setSender(sender);
			} else {
				indexOfMessage = indexOfHypen;
			}
			String message = conversationText.substring(indexOfMessage + 2);
			conversation.setMessage(message);
			String[] words = message.split(" ");
			for (String word : words) {
				if(word.isEmpty())
					continue;
				Integer count = wordCount.get(word);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				wordCount.put(word, count);
			}
			conversations.add(conversation);
		}
		System.out.println(conversations);
		System.out.println(wordCount);
	}

}
