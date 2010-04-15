<?xml version="1.0" encoding="UTF-8" ?>
<html xmlns="http://www.w3.org/1999/xhtml"
    xmlns:ui="http://java.sun.com/jsf/facelets"
    xmlns:h="http://java.sun.com/jsf/html"
    xmlns:f="http://java.sun.com/jsf/core">

        Title: <h:inputText value="#{articlesBean.currentArticle.title}" />
        <br />
        <h:inputTextarea value="#{articlesBean.currentArticle.text}" />
        <h:commandButton action="#{articlesBean.saveArticle}" />
</html>