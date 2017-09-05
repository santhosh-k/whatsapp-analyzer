package net.santhosh.entities;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.persistence.*;

@Entity
@Table(name="conversation")
public class Conversation {
	private int conversation_id;
	private Date date_;
	private String time_;
	private String sender = "";
	private String message;
	private boolean containsSmiley = false;

	public Conversation() {
	}

	/**
	 * @return the containsSmiley
	 */
	public boolean isContainsSmiley() {
		return containsSmiley;
	}

	/**
	 * @param containsSmiley the containsSmiley to set
	 */
	public void setContainsSmiley(boolean containsSmiley) {
		this.containsSmiley = containsSmiley;
	}

	/**
	 * @return the date
	 */
	public String getDate_() {
		return date_.toString();
	}

	/**
	 * @param date
	 *            the date to set
	 * @throws ParseException 
	 */
	public void setDate_(String date) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		this.date_ = new Date(formatter.parse(date).getTime());
	}

	/**
	 * @return the time
	 */
	public String getTime_() {
		return time_;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime_(String time) {
		this.time_ = time;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Conversation [conversation_id=" + conversation_id + ", date=" + date_ + ", time=" + time_ + ", sender="
				+ sender + ", message=" + message + ", containsSmiley=" + containsSmiley + "]";
	}

	@Id
	@Column(name = "conversation_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getConversation_id() {
		return conversation_id;
	}

	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}

}
