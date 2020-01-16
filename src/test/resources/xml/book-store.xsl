<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions">

<xsl:output omit-xml-declaration="yes" indent="yes" method="xml"/>

<xsl:template match="/">
<authors>
  <xsl:apply-templates select="/book-store/book/autor[not(last-name = preceding::last-name)]" />
</authors>
</xsl:template>

<xsl:template match="book">
  <book>
    <title><xsl:value-of select="title"/></title>
  </book>
</xsl:template>

<xsl:template match="autor" >
  <autor>
    <xsl:variable name="autor" select="."/>
    <name><xsl:value-of select="first-name"/><xsl:text> </xsl:text><xsl:value-of select="last-name"/></name>
    <books>
      <xsl:apply-templates select="/book-store/book[autor = $autor]" />
    </books>
  </autor>
</xsl:template>

</xsl:stylesheet>

