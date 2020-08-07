$("#previewSubject").html("${post.subject?html}");
$("#previewMessage").html("${post.text}");
$("#previewTable").show();

//Prism.highlightAll();
// This inlines everything: Using the Keep Markup plugin according to
// https://github.com/PrismJS/prism/issues/832 did not solve the issue, as now the linenumbers are out of whack.

var s = document.location.toString();
var index = s.indexOf("#preview");

if (index > -1) {
	s = s.substring(0, index);
}

document.location = s + "#preview";

