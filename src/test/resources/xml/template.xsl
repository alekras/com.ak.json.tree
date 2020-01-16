<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
{
  <xsl:apply-templates/>  
}
</xsl:template>

<xsl:template match="f1">
  "f1":"<xsl:value-of select="."/>",
</xsl:template>

<xsl:template match="f4">
  "f4":"<xsl:value-of select="."/>",
</xsl:template>

<xsl:template match="f5">
  "f5":"<xsl:value-of select="."/>"
</xsl:template>

<xsl:template match="f2">
  "f2":[<xsl:apply-templates select="_a"/>]
</xsl:template>

<xsl:template match="f3">
  {"f3":{<xsl:apply-templates select="f4"/><xsl:apply-templates select="f5"/>},
</xsl:template>

<xsl:template match="f6">
  "f6":"<xsl:value-of select="."/>"}
</xsl:template>

<xsl:template match="_a[not(./child::f3)]">
  <xsl:value-of select="."/>,
</xsl:template>

<xsl:template match="_a">
  <xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

