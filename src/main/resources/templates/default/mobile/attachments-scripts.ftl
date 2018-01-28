var panelOpen = false;
var total = 0;
var ignoreStart = false;
var maxAttachments = ${maxAttachments!0};
var counter = 0;

<#if attachmentsEnabled!false>
	var template = "<div id='attach_#counter#' class='gensmall'>";
    template += "<div>";
    template += "Filename: ";
	template += "<input type='file' name='file_#counter#'/>";
    template += "</div>";
    template += "<div>";
	template += "Description: ";
	template += "<input type='text' name='comment_#counter#' />";
	template += "<a href='javascript:removeAttachment(#counter#)' class='gensmall'>[Remove Attachment]</a>";
    template += "</div>";
	template += "<div style='border-top: 1px dashed #000;'>&nbsp;</div>";
    template += "</div>";

	function showAttachmentsPanel()
	{
		if (counter < maxAttachments) {
			var s = template.replace(/#counter#/g, total);
			$("#attachmentFields").append(s);
			$("#total_files").val(++total);

			counter++;
			setAddAttachmentButtonStatus();
		}
	}

	function removeAttachment(index)
	{
		$("#attach_" + index).empty();
		counter--;
        setAddAttachmentButtonStatus();
	}

	function setAddAttachmentButtonStatus()
	{
		var disabled = !(counter < maxAttachments);
		document.post.add_attach.disabled = disabled;
		document.post.add_attach.style.color = disabled ? "#cccccc" : "#000000";
	}
</#if>

<#if attachments??>
	var templateEdit = "<div class='row2 gen'>";
    templateEdit += "<div>";
    templateEdit += "Filename: #name#";
    templateEdit += "</div>";
    templateEdit += "<div>";
    templateEdit += "Description: ";
	templateEdit += "<input type='text' name='edit_comment_#id#' value='#value#'/>";
	templateEdit += "<span class='gensmall'><input type='checkbox' onclick='toggleAttachmentDeletion(#id#, this);'/>Remove Attachment</span>";
	templateEdit += "</div>";
    templateEdit += "<div style='border-top: 1px dashed #000;'>&nbsp;</div>";
    templateEdit += "</div>";

    function showAttachmentsPanelForEdit()
	{
		var data = new Array();
		<#list attachments as a>
			var attach_${a.id} = new Array();

			attach_${a.id}["filename"] = "${a.info.realFilename}";
			attach_${a.id}["description"] = "${a.info.comment}";
			attach_${a.id}["id"] = "${a.id}";

			data.push(attach_${a.id});
		</#list>

		counter = data.length;
		<#if attachmentsEnabled!false>setAddAttachmentButtonStatus();</#if>

		for (var i = 0; i < data.length; i++) {
			var a = data[i];
			var s = templateEdit.replace(/#value#/, a["description"]);
			s = s.replace(/#name#/, a["filename"]);
			s = s.replace(/#id#/g, a["id"]);

			var v = document.getElementById("edit_attach").innerHTML;
			v += s;
			document.getElementById("edit_attach").innerHTML = v;
			document.post.edit_attach_ids.value += a["id"] + ",";
		}
	}

	function toggleAttachmentDeletion(id, f)
	{
		if (f.checked) {
			document.post.delete_attach.value += id + ",";
		}
		else {
			var p = document.post.delete_attach.value.split(",");
			document.post.delete_attach.value = "";
			for (var i = 0; i < p.length; i++) {
				if (p[i] != id) {
					document.post.delete_attach.value += p[i] + ",";
				}
			}
		}
	}
</#if>