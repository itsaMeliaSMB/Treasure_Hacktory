package com.treasurehacktory

import android.content.Context
import com.treasurehacktory.model.Hoard
import com.treasurehacktory.repository.HMRepository
import java.io.File
import java.lang.Math.abs

class LootReporter(private val context : Context, private val repository: HMRepository){

    val darkBlock = "▓"
    val semidarkBlock = "▒"

    /**
     *
     */
    suspend fun exportHoardContentsAsPlainText(hoard: Hoard, _pageWidth: Int = 80 ): String {

        val hoardGems = repository.getGemsOnce(hoard.hoardID)
        val hoardArt = repository.getArtObjectOnce(hoard.hoardID)
        val hoardItems = repository.getMagicItemsOnce(hoard.hoardID)
        val hoardSpCos = repository.getSpellCollectionOnce(hoard.hoardID)
        val hoardEvents = repository.getHoardEventsOnce(hoard.hoardID)

        //TODO rename later
        val fileName = "hoardreport.txt"
        val reportFile = File(fileName)

        // region [ Extension functions ]
        val pageWidth = _pageWidth.coerceIn(50,100) // must be at least 7

        /**
         * Splits a string longer than pageWidth without splitting words or stat blocks, padding non-initial substrings to indent level.
         *
         * @param _prefix String to add to start of this string. Will not be split unless it is longer than page width.
         * @param _startIndent How many "tabs" to add to the start of the prefix.
         * @param _prefixEndIndent What "tab" level to pad the end of the prefix to. if
         * @param _suffix String to add to start of this string. Will not be split unless it is longer than pageWidth plus new line padding.
         * @param _newLineIndentAddend How many additional "tabs" to add to the start of any lines beyond the first.
         * @param padFirstLineToPrefixEnd If set to true, add padding to the end of prefix to make it a multiple of padAmt.
         * @param padNewLinesToPrefixEnd If set to true, ignore [_newLineIndentAddend] and pad the start of new lines to the end of full prefix.
         * @param _padAmt How many characters of whitespace each indent should be (maximum of 8).
         * @param _bullet Character to use as bullet. If null, do not use a bullet for this line.
         */
        fun String.formatLineToPrintout(_prefix: String = "",_startIndent: Int = 0,
                                        _prefixEndIndent: Int = 0,_suffix: String = "",
                                        _newLineIndentAddend: Int = 1,
                                        padFirstLineToPrefixEnd: Boolean = true,
                                        padNewLinesToPrefixEnd: Boolean = true,
                                        _padAmt: Int = 4, _bullet: String = "•"): String {

            val padAmt = _padAmt.coerceIn(1,8)
            val startIndent = _startIndent.coerceIn(0, pageWidth / padAmt - 1)

            fun getMinPrefixLength(): Int {

                var minimumLength = 0
                val prefixEndIndent = _prefixEndIndent.coerceAtLeast(startIndent)

                // Account for start index
                minimumLength += padAmt * startIndent

                // Account for prefix string's length
                if (_prefix.isNotEmpty() && padFirstLineToPrefixEnd) {

                    minimumLength += if (_prefix.length < padAmt) {
                        _prefix.length + abs(_prefix.length - padAmt)
                    } else {
                        _prefix.length + (_prefix.length % padAmt)
                    }
                }

                // If longer than provided indent level, use instead of provided level
                return if (minimumLength >= prefixEndIndent * padAmt) {
                    minimumLength
                } else {
                    prefixEndIndent * padAmt
                }
            }

            val paddedPrefixLength = getMinPrefixLength()

            val prefix = (" ".repeat(startIndent * padAmt) + _prefix)
                .padEnd(paddedPrefixLength)
                .takeLast(pageWidth - 3)

            val bullet = if (_bullet.isNotBlank()) { _bullet.take(1) + " " } else ""
            val bulletPad = if (bullet.isNotBlank()) 2 else 0

            val newLineIndent = (startIndent + _newLineIndentAddend).coerceIn(0, pageWidth / padAmt - 1)
            val suffix = _suffix.replace("\n","")

            val leadingSpaceCount = if (padNewLinesToPrefixEnd) {
                paddedPrefixLength + bulletPad
            } else {
                newLineIndent * padAmt + bulletPad
            }

            var workingString   = prefix + bullet + this
            val parsedString    = StringBuilder()

            /** Adds leadingSpaceCount spaces to the start of string */
            fun String.padNewLineStart(): String = " ".repeat(leadingSpaceCount) + this

            /** Extension of [String.substring] that corrects out-of-bounds index parameters */
            fun String.safeSubstring(startIndex: Int, endIndex: Int): String {

                return if (this.isEmpty()) {

                    ""

                } else {

                    val effectiveStartIndex = if (this.length < startIndex) this.length - 1 else startIndex
                    val effectiveEndIndex = if (this.length < endIndex) this.length else endIndex

                    this.substring(effectiveStartIndex,effectiveEndIndex)
                }
            }

            if (workingString.length > pageWidth) {

                // region [ Splice the first string ]

                when {

                    workingString.safeSubstring(prefix.length,pageWidth).contains("\n") -> {

                        // Split at line return, even if it could split at a space.
                        parsedString.append(workingString.substringBefore("\n"))
                        workingString = workingString.substringAfter("\n")
                    }

                    workingString.safeSubstring(prefix.length + bulletPad,pageWidth).contains(' ') -> {

                        // Split at last space before page end
                        val lastSpaceIndex = workingString.safeSubstring(0,pageWidth).lastIndexOf(' ')

                        parsedString.append(workingString.take(lastSpaceIndex))
                        workingString = workingString.drop(lastSpaceIndex + 1)
                    }

                    else -> {

                        if (prefix.length > pageWidth && prefix.contains(' ')) {

                            // Split at last space, even though it's in the prefix.
                            parsedString.append(workingString.substringBeforeLast(' '))
                            workingString = workingString.substringAfterLast(' ')

                        } else {

                            // Split at page width without concern for cutting off a word or affix.
                            parsedString.append(workingString.take(pageWidth))
                            workingString = workingString.drop(pageWidth)
                        }
                    }
                }
                // endregion

                // region [ Pad and splice remaining lines ]

                while (workingString.isNotBlank()){

                    workingString = workingString.padNewLineStart()

                    when {

                        workingString.take(pageWidth).contains("\n") -> {

                            parsedString.append("\n" + workingString.substringBefore("\n"))
                            workingString = workingString.substringAfter("\n")
                        }

                        workingString.length <= pageWidth -> {

                            if (workingString.length + suffix.length < pageWidth) {

                                parsedString.append("\n" + workingString + suffix)

                            } else {

                                if (suffix.length + leadingSpaceCount > pageWidth){

                                    var workingSuffix = suffix

                                    // Iterate and split until suffix fully appended
                                    while (workingSuffix.isNotBlank()) {

                                        // Pad start of suffix string
                                        workingSuffix = workingSuffix.padNewLineStart()

                                        if (workingSuffix.length > pageWidth){

                                            // Split suffix
                                            if (workingSuffix.safeSubstring(leadingSpaceCount,pageWidth).contains(' ')){

                                                // Split at space
                                                parsedString.append("\n" + workingSuffix.substringBeforeLast(' '))
                                                workingSuffix = workingSuffix.substringAfterLast(' ')

                                            } else {

                                                // Split at edge otherwise
                                                parsedString.append("\n" + workingSuffix.take(pageWidth))
                                                workingSuffix = workingSuffix.drop(pageWidth)
                                            }

                                        } else {

                                            parsedString.append("\n" + workingSuffix)
                                            workingSuffix = ""
                                        }
                                    }

                                } else {

                                    // Put suffix on its own line
                                    parsedString.append("\n" + suffix.padNewLineStart())
                                }
                            }

                            workingString = ""
                        }

                        workingString.safeSubstring(leadingSpaceCount,pageWidth).contains(' ') -> {

                            val lastSpaceIndex = workingString.take(pageWidth).lastIndexOf(' ')

                            parsedString.append("\n" + workingString.take(lastSpaceIndex))
                            workingString = workingString.drop(lastSpaceIndex + 1)
                        }

                        else -> {

                            // Split at page width without concern for cutting off a word.
                            parsedString.append("\n" + workingString.take(pageWidth))
                            workingString = workingString.drop(pageWidth)
                        }
                    }
                }
                // endregion

            } else {

                if ((workingString + suffix).length > pageWidth) {

                    fun appendSuffixOnNewLine() {

                        if (suffix.length + leadingSpaceCount > pageWidth){

                            var workingSuffix = suffix

                            // Iterate and split until suffix fully appended
                            while (workingSuffix.isNotBlank()) {

                                // Pad start of suffix string
                                workingString = workingSuffix.padNewLineStart()

                                if (workingSuffix.length > pageWidth){

                                    // Split suffix
                                    if (workingSuffix.substring(leadingSpaceCount).contains(' ')){

                                        // Split at space
                                        parsedString.append("\n" + workingSuffix.substringBeforeLast(' '))
                                        workingString = workingSuffix.substringAfterLast(' ')

                                    } else {

                                        // Split at edge otherwise
                                        parsedString.append(workingSuffix.take(pageWidth))
                                        workingSuffix = "\n" + workingSuffix.drop(pageWidth)
                                    }

                                } else {

                                    parsedString.append("\n" + workingSuffix)
                                    workingSuffix = ""
                                }
                            }

                        } else {

                            // Put suffix on its own line
                            parsedString.append("\n" + suffix.padNewLineStart())
                        }
                    }

                    fun String.fitsSuffixOnNewLine(): Boolean {

                        return (leadingSpaceCount + this.length + suffix.length) <= pageWidth
                    }

                    // Find split point for main string before adding suffix
                    if (workingString.substring(prefix.lastIndex).contains("\n")){

                        // Split at line return, even if it could split at a space.
                        parsedString.append(workingString.substringBefore("\n"))
                        workingString = workingString.substringAfter("\n").padNewLineStart()

                        // Account for all line returns first.
                        while (workingString.contains("\n")) {

                            // Check if newly-padded string must first be split before line return is within the top line.
                            if (workingString.indexOf("\n") > pageWidth) {

                                if (workingString.drop(leadingSpaceCount).contains(' ')) {

                                    // Split at last whitespace
                                    parsedString.append("\n" + workingString.substringBeforeLast(' '))
                                    workingString = workingString.substringAfterLast(' ')

                                } else {

                                    // Split at page width
                                    parsedString.append("\n" + workingString.take(pageWidth))
                                    workingString = workingString.drop(pageWidth)
                                }

                            } else {

                                // String does not need to be split to reach line return, so split as normal.
                                parsedString.append("\n" + workingString.substringBefore("\n"))
                                workingString = workingString.substringAfter("\n")
                            }

                            // Pad start of remaining string regardless of split
                            workingString = workingString.padNewLineStart()
                        }
                    }

                    // Split at last space before page width, if still necessary.
                    if ((workingString + suffix).length > pageWidth){

                        // Check if workingString contains a space suitable for a split point.
                        if (workingString.safeSubstring(prefix.lastIndex,pageWidth).contains(' ')) {

                            // Split at last space before page end
                            val lastSpaceIndex = workingString.take(pageWidth).lastIndexOf(' ')

                            parsedString.append(workingString.take(lastSpaceIndex))
                            workingString = workingString.drop(lastSpaceIndex + 1)

                            if (workingString.fitsSuffixOnNewLine()) {

                                parsedString.append("\n" + workingString.padNewLineStart() + suffix)

                            } else {

                                parsedString.append("\n" + workingString.padNewLineStart())
                                appendSuffixOnNewLine()
                            }

                        } else {

                            // Split at page width without concern for cutting off a word or affix.
                            parsedString.append(workingString.take(pageWidth))
                            workingString = workingString.drop(pageWidth)

                            if (workingString.fitsSuffixOnNewLine()) {

                                parsedString.append("\n" + workingString.padNewLineStart() + suffix)

                            } else {

                                parsedString.append("\n" + workingString.padNewLineStart())
                                appendSuffixOnNewLine()
                            }
                        }

                    } else {

                        parsedString.append(workingString + suffix)
                    }

                } else {

                    var fullString = workingString + suffix

                    if (fullString.contains("\n")) {

                        // Split first instance of line return
                        parsedString.append(fullString.substringBefore("\n"))
                        fullString = fullString.substringAfter("\n").padNewLineStart()

                        // Split the string as indicated, but indent as specified.
                        while (fullString.contains("\n")){

                            // Split at line return, even if it could split at a space.
                            parsedString.append("\n" + fullString.substringBefore("\n"))
                            fullString = fullString.substringAfter("\n").padNewLineStart()
                        }

                        if (fullString.isNotEmpty()) parsedString.append("\n" + fullString)

                    } else {

                        // Return string with affixes and no line splits
                        return workingString + suffix
                    }
                }
            }

            return parsedString.toString()
        }

        /**
         * Centers this string in a bar the length of pageWidth, splitting the string into multiple lines if it is too long.
         *
         * @param _barPattern What string to use as a repeating pattern for the bar.
         * @param _maxStrLength The longest a string can without be split and moved onto a new line.
         * @param _minMargin Minimum number of spaces to between each side of the string and the bar, if it does not conflict with [_maxStrLength].
         * @param _boxThickness If greater than zero, treat this as a box instead with an edge width of this much. Ignored otherwise.
         */
        fun String.centerInBar(_barPattern: String = "*", _maxStrLength: Int = 78, _minMargin: Int = 1, _boxThickness: Int = 0): String {


            val barPattern = if (_barPattern.isNotBlank()) _barPattern.take(pageWidth) else "*"

            val solidBar = barPattern.repeat((pageWidth / barPattern.length) + 1).take(pageWidth)

            if (this.replace("\n","").isNotEmpty()) {

                val maxStrLength : Int
                val minMargin : Int
                val boxThickness : Int
                var emptyBoxRow = ""

                if (_boxThickness > 0) {

                    boxThickness =
                        if (_boxThickness * 2 <= pageWidth - 5) _boxThickness.coerceAtLeast(1) else _boxThickness.coerceAtMost((pageWidth - 4))/2

                    maxStrLength =
                        if (_maxStrLength <= pageWidth - (boxThickness * 2)) _maxStrLength.coerceAtLeast(3) else pageWidth - (boxThickness * 2 + 2)

                    minMargin = pageWidth - maxStrLength - (boxThickness * 2)

                    emptyBoxRow = solidBar.take(boxThickness) + " ".repeat(pageWidth - boxThickness * 2) + solidBar.takeLast(boxThickness)

                } else {

                    boxThickness = 0
                    maxStrLength = if (_maxStrLength <= pageWidth - 4) _maxStrLength.coerceAtLeast(1) else pageWidth - 4
                    minMargin = _minMargin.takeUnless { (it + 1) * 2 > pageWidth - maxStrLength || it < 1 } ?: 1
                }

                if (this.length > maxStrLength || this.contains("\n")) {

                    var workingString = this
                    val splitStrings = ArrayList<String>()

                    // Split the string into multiple lines
                    while (workingString.isNotEmpty()){

                        val leadingChunk = workingString.take(maxStrLength)

                        if (leadingChunk.length < maxStrLength) {

                            if (leadingChunk.contains("\n")) {
                                // split at line return
                                splitStrings.add(workingString.substringBefore("\n"))
                                workingString = workingString.substringAfter("\n")
                            } else {
                                // add without splitting
                                splitStrings.add(workingString)
                                workingString = ""
                            }

                        } else {

                            when {
                                leadingChunk.contains("\n") -> {
                                    // split at line return
                                    splitStrings.add(workingString.substringBefore("\n"))
                                    workingString = workingString.substringAfter("\n")
                                }
                                leadingChunk.contains(' ') -> {
                                    // split at space
                                    val lastSpaceIndex = leadingChunk.lastIndexOf(' ')

                                    splitStrings.add(workingString.take(lastSpaceIndex))
                                    workingString = workingString.drop(lastSpaceIndex)
                                }
                                leadingChunk.contains('-') -> {
                                    // split after dash
                                    val lastDashIndex = leadingChunk.lastIndexOf('-')

                                    splitStrings.add(workingString.take(lastDashIndex + 1))
                                    workingString = workingString.drop(lastDashIndex)
                                }
                                else -> {
                                    // split without regard for cutting off words
                                    splitStrings.add(workingString.take(maxStrLength))
                                    workingString = workingString.drop(maxStrLength)
                                }
                            }
                        }
                    }

                    // Find length of the longest substring
                    val longestStrLength = (splitStrings.maxOf { it.length })

                    // Build new string with line returns
                    val barBuilder = StringBuilder()

                    if (_boxThickness > 0) {

                        barBuilder.append(solidBar + "\n")
                        barBuilder.append(emptyBoxRow)

                        splitStrings.forEach{ entry ->

                            val startPad = ( pageWidth - (boxThickness * 2) - entry.length ) / 2
                            val endPad = if (entry.length % 2 == pageWidth % 2) startPad else startPad + 1

                            val paddedString = " ".repeat(startPad) + entry + " ".repeat(endPad)

                            barBuilder.append(
                                "\n" + solidBar.take(boxThickness) + paddedString + solidBar.takeLast(boxThickness)
                            )
                        }

                        barBuilder.append("\n" + emptyBoxRow)
                        barBuilder.append("\n" + solidBar)

                    } else {

                        splitStrings.forEachIndexed { index, entry ->

                            val paddedString = if (entry.length < longestStrLength) {

                                val startPad = minMargin + (longestStrLength - entry.length) / 2
                                val endPad = if (entry.length % 2 != longestStrLength % 2) startPad + 1 else startPad

                                " ".repeat(startPad) + entry + " ".repeat(endPad)

                            } else {

                                " ".repeat(minMargin) + entry + " ".repeat(minMargin)
                            }

                            val barChunkLength = (pageWidth - paddedString.length) / 2

                            if (index != 0) { barBuilder.append("\n") }

                            barBuilder.append(
                                solidBar.take(barChunkLength) + paddedString + solidBar.drop(barChunkLength + paddedString.length))
                        }
                    }

                    // Return multi-line bar
                    return barBuilder.toString()

                } else {

                    if (_boxThickness > 0) {

                        val startPad = ( pageWidth - (boxThickness * 2) - this.length ) / 2
                        val endPad = if (this.length % 2 == pageWidth % 2) startPad else startPad + 1

                        val paddedString = " ".repeat(startPad) + this + " ".repeat(endPad)

                        return solidBar + "\n" + emptyBoxRow + "\n" +
                                solidBar.take(boxThickness) + paddedString + solidBar.takeLast(boxThickness) +
                                "\n" + emptyBoxRow + "\n" + solidBar

                    } else {

                        val paddedString = " ".repeat(minMargin) + this + " ".repeat(minMargin)
                        val barChunkLength = (pageWidth - paddedString.length) / 2

                        return solidBar.take(barChunkLength) + paddedString + solidBar.drop(barChunkLength + paddedString.length)
                    }
                }

            } else {

                return solidBar
            }
        }
        // endregion

        // region [ Prepare file ] TODO

        // TODO Left off here, but despite that, forget about the plaintext export for now. Instead,
        //  focus on getting the unit tests for hoard generation set up and going, including test
        //  implementations of Daos in Room as necessary. Goal is confirming template queries work
        //  and no exceptions are thrown within possible parameters for orders.

        // endregion

        // region [ Header block ] TODO



        // endregion

        // region [ Coinage block ] TODO
        // endregion

        // region [ Gem block ] TODO
        // endregion

        // region [ ArtObject block ] TODO
        // endregion

        // region [ SpellCollection block ] TODO
        // endregion

        // region [ Event block ] TODO
        // endregion

        // region [ Export file itself ] TODO
        // endregion

        return "lol" // TODO fix/remove return type
    }
}