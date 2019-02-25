<#if !doc?exists>
	alert("${I18n.getMessage("No")}");
<#else>
	var message = "Post ID: ${doc.get("post.id")}\n";
	message += "Topic ID: ${doc.get("topic.id")}\n";

	var date = new Date(${doc.get("date")});
	var month = 1 + date.getMonth();
	if (month < 10)
		month = "0" + month;
	var day = date.getDate();
	if (day < 10)
		day = "0" + day;
	var hours = date.getHours();
	if (hours < 10)
		hours = "0" + hours;
	var minutes = date.getMinutes();
	if (minutes < 10)
		minutes = "0" + minutes;
	var seconds = date.getSeconds();
	if (seconds < 10)
		seconds = "0" + seconds;
	var date2 = date.getFullYear() + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;

	alert(message + "Date (yyyy/MM/dd HH:mm:ss): " + date2);
</#if>
