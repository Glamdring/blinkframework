<?xml version="1.0" encoding="UTF-8" ?>
<html xmlns="http://www.w3.org/1999/xhtml"
    xmlns:ui="http://java.sun.com/jsf/facelets"
    xmlns:h="http://java.sun.com/jsf/html"
    xmlns:f="http://java.sun.com/jsf/core">

    <h:commandLink value="New article" target="#{articlesBean.newArticle}" />
    <ui:repeat value="#{articlesBean.articles}" var="article">
        <h1>#{article.title}</h1>
        <h:outputText value="#{article.date}">
            <f:convertDateTime pattern="dd.MM.yyyy hh:mm" />
        </h:outputText>
        <br />
        <h:outputText value="#{article.text}" />
        <hr />
    </ui:repeat>
</html>