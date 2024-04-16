package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.io.FileType;
import groovy.lang.Closure;
import groovy.transform.NamedParam;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Ad file wrapper which resolves its path to minecraft home and checks if the file really is in minecraft home.
 */
public final class GroovyFile implements Comparable<GroovyFile> {

    public static boolean isPathAccessible(String path) {
        if (path.startsWith(GroovyScript.getMinecraftHome().getPath())) return true;
        // examples folder is not in minecraft home
        return FMLLaunchHandler.isDeobfuscatedEnvironment() && path.startsWith(GroovyScript.getMinecraftHome().getParent());
    }

    private static final File INVALID = new File("");

    @GroovyBlacklist
    private final File internal;
    private final boolean accessible;

    public GroovyFile(GroovyFile file) {
        this.internal = file.internal;
        this.accessible = file.accessible;
    }

    public GroovyFile(File internal) {
        File file;
        boolean accessible;
        try {
            file = GroovyScript.getMinecraftHome().toPath().resolve(internal.toPath()).toFile().getCanonicalFile();
            accessible = isPathAccessible(file.getPath());
        } catch (IOException e) {
            GroovyLog.get().error("Failed to resolve File('{}')", internal.toPath());
            file = INVALID;
            accessible = false;
        }
        this.internal = file;
        this.accessible = accessible;
    }

    public GroovyFile(String path) {
        this(new java.io.File(path));
    }

    public GroovyFile(String parent, String child) {
        this(new File(parent), child);
    }

    public GroovyFile(File parent, String child) {
        this(new File(parent, child));
    }

    public GroovyFile(GroovyFile parent, String child) {
        this(new File(parent.internal, child));
    }

    public GroovyFile(URI uri) {
        this(new File(uri));
    }

    public GroovyFile(String... parts) {
        this(new File(FileUtil.makePath(parts)));
    }

    public boolean isInvalid() {
        return this.internal == INVALID;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void checkAccessible() {
        if (isInvalid()) {
            throw new IllegalStateException("Can't access a file which failed to resolve.");
        }
        if (!this.accessible) {
            throw new SecurityException("Only files in minecraft home and sub directories can be accessed from scripts! Tried to access " + this.internal.getPath());
        }
    }

    public String getPath() {
        return this.internal.getPath();
    }

    public boolean exists() {
        return this.internal.exists();
    }

    public boolean isFile() {
        return this.internal.isFile();
    }

    public boolean isDirectory() {
        return this.internal.isDirectory();
    }

    public boolean canRead() {
        return isAccessible() && this.internal.canRead();
    }

    public boolean canWrite() {
        return isAccessible() && this.internal.canWrite();
    }

    public boolean canExecute() {
        return false;
    }

    public long lastModified() {
        return this.internal.lastModified();
    }

    public long length() {
        return this.internal.length();
    }

    public boolean createNewFile() throws IOException {
        checkAccessible();
        return this.internal.createNewFile();
    }

    public boolean delete() {
        checkAccessible();
        return this.internal.delete();
    }

    public String[] list() {
        checkAccessible();
        return this.internal.list();
    }

    public GroovyFile[] listFiles() {
        checkAccessible();
        File[] files = this.internal.listFiles();
        if (files == null) return null;
        return ArrayUtils.map(files, GroovyFile::new, new GroovyFile[files.length]);
    }

    public boolean mkdir() {
        checkAccessible();
        return this.internal.mkdir();
    }

    public boolean mkdirs() {
        checkAccessible();
        return this.internal.mkdirs();
    }

    public String getCanonicalPath() {
        return getPath();
    }

    public GroovyFile getCanonicalFile() {
        return this; // is already canonical
    }

    @Override
    public int compareTo(@NotNull GroovyFile o) {
        return this.internal.compareTo(o.internal);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == GroovyFile.class) {
            return isInvalid() == ((GroovyFile) obj).isInvalid() || this.internal.equals(((GroovyFile) obj).internal);
        }
        if (obj instanceof File file) {
            return !isInvalid() && this.internal.equals(file);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.internal.hashCode();
    }

    @Override
    public String toString() {
        return isInvalid() ? "INVALID" : this.internal.toString();
    }

    // Groovy methods

    public <T> T eachLine(Closure<T> closure) throws IOException {
        return eachLine("UTF-8", 0, closure);
    }

    public <T> T eachLine(int firstLine, Closure<T> closure) throws IOException {
        return eachLine("UTF-8", firstLine, closure);
    }

    public <T> T eachLine(String charset, Closure<T> closure) throws IOException {
        return eachLine(charset, 0, closure);
    }

    public <T> T eachLine(String charset, int firstLine, Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.eachLine(this.internal, charset, firstLine, closure);
    }

    public BufferedReader newReader() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.newReader(this.internal, "UTF-8");
    }

    /**
     * Create a buffered reader for this file, using the specified
     * charset as the encoding.
     *
     * @param charset the charset for this File
     * @return a BufferedReader
     * @throws FileNotFoundException        if the File was not found
     * @throws UnsupportedEncodingException if the encoding specified is not supported
     */
    public BufferedReader newReader(String charset) throws FileNotFoundException, UnsupportedEncodingException {
        checkAccessible();
        return ResourceGroovyMethods.newReader(this.internal, charset);
    }

    /**
     * Create a new BufferedReader for this file and then
     * passes it into the closure, ensuring the reader is closed after the
     * closure returns.
     *
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     */
    public <T> T withReader(@ClosureParams(value = SimpleType.class, options = "java.io.BufferedReader") Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withReader(this.internal, "UTF-8", closure);
    }

    /**
     * Create a new BufferedReader for this file using the specified charset and then
     * passes it into the closure, ensuring the reader is closed after the
     * closure returns.
     *
     * @param charset the charset for this input stream
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     */
    public <T> T withReader(String charset,
                            @ClosureParams(value = SimpleType.class, options = "java.io.BufferedReader") Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withReader(this.internal, charset, closure);
    }

    /**
     * Create a buffered output stream for this file.
     *
     * @return the created OutputStream
     * @throws IOException if an IOException occurs.
     */
    public BufferedOutputStream newOutputStream() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.newOutputStream(this.internal);
    }

    /**
     * Creates a new OutputStream for this file and passes it into the closure.
     * This method ensures the stream is closed after the closure returns.
     *
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     * @see IOGroovyMethods#withStream(java.io.OutputStream, groovy.lang.Closure)
     */
    public Object withOutputStream(@ClosureParams(value = SimpleType.class, options = "java.io.OutputStream") Closure<?> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withOutputStream(this.internal, closure);
    }

    /**
     * Create a new InputStream for this file and passes it into the closure.
     * This method ensures the stream is closed after the closure returns.
     *
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     * @see IOGroovyMethods#withStream(java.io.InputStream, groovy.lang.Closure)
     */
    public Object withInputStream(@ClosureParams(value = SimpleType.class, options = "java.io.InputStream") Closure<?> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withInputStream(this.internal, closure);
    }


    /**
     * Create a buffered writer for this file.
     *
     * @return a BufferedWriter
     * @throws IOException if an IOException occurs.
     */
    public BufferedWriter newWriter() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.newWriter(this.internal, "UTF-8");
    }

    /**
     * Creates a buffered writer for this file, optionally appending to the
     * existing file content.
     *
     * @param append true if data should be appended to the file
     * @return a BufferedWriter
     * @throws IOException if an IOException occurs.
     */
    public BufferedWriter newWriter(boolean append) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.newWriter(this.internal, "UTF-8", append);
    }

    /**
     * Helper method to create a buffered writer for a file without writing a BOM.
     *
     * @param charset the name of the encoding used to write in this file
     * @param append  true if in append mode
     * @return a BufferedWriter
     * @throws IOException if an IOException occurs.
     */
    public BufferedWriter newWriter(String charset, boolean append) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.newWriter(this.internal, charset, append);
    }

    /**
     * Creates a buffered writer for this file, writing data without writing a
     * BOM, using a specified encoding.
     *
     * @param charset the name of the encoding used to write in this file
     * @return a BufferedWriter
     * @throws IOException if an IOException occurs.
     */
    public BufferedWriter newWriter(String charset) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.newWriter(this.internal, charset);
    }

    /**
     * Creates a new BufferedWriter for this file, passes it to the closure, and
     * ensures the stream is flushed and closed after the closure returns.
     *
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     */
    public <T> T withWriter(@ClosureParams(value = SimpleType.class, options = "java.io.BufferedWriter") Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withWriter(this.internal, "UTF-8", closure);
    }

    /**
     * Creates a new BufferedWriter for this file, passes it to the closure, and
     * ensures the stream is flushed and closed after the closure returns.
     * The writer will use the given charset encoding.  If the given charset is
     * "UTF-16BE" or "UTF-16LE" (or an equivalent alias), the requisite byte
     * order mark is written to the stream when the writer is created.
     *
     * @param charset the charset used
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     */
    public <T> T withWriter(String charset,
                            @ClosureParams(value = SimpleType.class, options = "java.io.BufferedWriter") Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withWriter(this.internal, charset, closure);
    }

    /**
     * Create a new BufferedWriter which will append to this file.  If the
     * given charset is "UTF-16BE" or "UTF-16LE" (or an equivalent alias), the
     * requisite byte order mark is written to the stream when the writer is
     * created.  The writer is passed to the closure and will be closed before
     * this method returns.
     *
     * @param charset the charset used
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     */
    public <T> T withWriterAppend(String charset,
                                  @ClosureParams(value = SimpleType.class, options = "java.io.BufferedWriter") Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withWriterAppend(this.internal, charset, closure);
    }

    /**
     * Create a new BufferedWriter for this file in append mode.  The writer
     * is passed to the closure and is closed after the closure returns.
     *
     * @param closure a closure
     * @return the value returned by the closure
     * @throws IOException if an IOException occurs.
     */
    public <T> T withWriterAppend(@ClosureParams(value = SimpleType.class, options = "java.io.BufferedWriter") Closure<T> closure) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.withWriterAppend(this.internal, "UTF-8", closure);
    }

    /**
     * Creates a buffered input stream for this file.
     *
     * @return a BufferedInputStream of the file
     * @throws FileNotFoundException if the file is not found.
     */
    public BufferedInputStream newInputStream() throws FileNotFoundException {
        checkAccessible();
        return ResourceGroovyMethods.newInputStream(this.internal);
    }

    /**
     * Reads the file into a list of Strings, with one item for each line.
     *
     * @return a List of lines
     * @throws IOException if an IOException occurs.
     * @see IOGroovyMethods#readLines(java.io.Reader)
     */
    public List<String> readLines() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.readLines(this.internal, "UTF-8");
    }

    /**
     * Reads the file into a list of Strings, with one item for each line.
     *
     * @param charset opens the file with a specified charset
     * @return a List of lines
     * @throws IOException if an IOException occurs.
     * @see IOGroovyMethods#readLines(java.io.Reader)
     */
    public List<String> readLines(String charset) throws IOException {
        return ResourceGroovyMethods.readLines(this.internal, charset);
    }

    /**
     * Read the content of the File using the specified encoding and return it
     * as a String.
     *
     * @param charset the charset used to read the content of the file
     * @return a String containing the content of the file
     * @throws IOException if an IOException occurs.
     */
    public String getText(String charset) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.getText(this.internal, charset);
    }

    /**
     * Read the content of the File and returns it as a String.
     *
     * @return a String containing the content of the file
     * @throws IOException if an IOException occurs.
     */
    public String getText() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.getText(this.internal, "UTF-8");
    }

    /**
     * Read the content of the File and returns it as a byte[].
     *
     * @return the bytes of the file content
     * @throws IOException if an IOException occurs.
     */
    public byte[] getBytes() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.getBytes(this.internal);
    }

    // 'as String[]' operator
    public <T> T asType(Class<T> clazz) {
        checkAccessible();
        return ResourceGroovyMethods.asType(this.internal, clazz);
    }

    /**
     * Write the bytes from the byte array to the File.
     *
     * @param bytes the byte[] to write to the file
     * @throws IOException if an IOException occurs.
     */
    public void setBytes(byte[] bytes) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.setBytes(this.internal, bytes);
    }

    /**
     * Creates, if needed, any parent directories for this File.
     *
     * @return itself
     * @throws IOException if the parent directories couldn't be created
     */
    public File createParentDirectories() throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.createParentDirectories(this.internal);
    }

    /**
     * Write the text to the File without writing a BOM.
     *
     * @param text the text to write to the File
     * @throws IOException if an IOException occurs.
     */
    public void write(String text) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.write(this.internal, text, "UTF-8");
    }

    /**
     * Synonym for write(text) allowing file.text = 'foo'.
     *
     * @param text the text to write to the File
     * @throws IOException if an IOException occurs.
     * @see ResourceGroovyMethods#write(java.io.File, java.lang.String)
     */
    public void setText(String text) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.setText(this.internal, text, "UTF-8");
    }

    /**
     * Synonym for write(text, charset) allowing:
     * <pre>
     * myFile.setText('some text', charset)
     * </pre>
     * or with some help from <code>ExpandoMetaClass</code>, you could do something like:
     * <pre>
     * myFile.metaClass.setText = { String s {@code ->} delegate.setText(s, 'UTF-8') }
     * myfile.text = 'some text'
     * </pre>
     *
     * @param charset The charset used when writing to the file
     * @param text    The text to write to the File
     * @throws IOException if an IOException occurs.
     * @see ResourceGroovyMethods#write(java.io.File, java.lang.String, java.lang.String)
     * @since 1.7.3
     */
    public void setText(String text, String charset) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.write(this.internal, charset);
    }

    /**
     * Write the text to the File.
     *
     * @param text the text to write to the File
     * @return the original file
     * @throws IOException if an IOException occurs.
     */
    public File leftShift(Object text) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.leftShift(this.internal, text);
    }

    /**
     * Write bytes to a File.
     *
     * @param bytes the byte array to append to the end of the File
     * @return the original file
     * @throws IOException if an IOException occurs.
     */
    public File leftShift(byte[] bytes) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.leftShift(this.internal, bytes);
    }

    /**
     * Append binary data to the file.  See {@link ResourceGroovyMethods#append(java.io.File, java.io.InputStream)}
     *
     * @param data an InputStream of data to write to the file
     * @return the file
     * @throws IOException if an IOException occurs.
     */
    public File leftShift(InputStream data) throws IOException {
        checkAccessible();
        return ResourceGroovyMethods.leftShift(this.internal, data);
    }

    /**
     * Write the text to the File without writing a BOM,
     * using the specified encoding.
     *
     * @param text    the text to write to the File
     * @param charset the charset used
     * @throws IOException if an IOException occurs.
     */
    public void write(String text, String charset) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.write(this.internal, text, charset);
    }

    /**
     * Append the text at the end of the File without writing a BOM.
     *
     * @param text the text to append at the end of the File
     * @throws IOException if an IOException occurs.
     */
    public void append(Object text) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, text, "UTF-8");
    }

    /**
     * Append the text supplied by the Writer at the end of the File without writing a BOM.
     *
     * @param reader the Reader supplying the text to append at the end of the File
     * @throws IOException if an IOException occurs.
     */
    public void append(Reader reader) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, reader, "UTF-8");
    }

    /**
     * Append the text supplied by the Writer at the end of the File without writing a BOM.
     *
     * @param writer the Writer supplying the text to append at the end of the File
     * @throws IOException if an IOException occurs.
     */
    public void append(Writer writer) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, writer, "UTF-8");
    }

    /**
     * Append bytes to the end of a File.  It <strong>will not</strong> be
     * interpreted as text.
     *
     * @param bytes the byte array to append to the end of the File
     * @throws IOException if an IOException occurs.
     */
    public void append(byte[] bytes) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, bytes);
    }

    /**
     * Append binary data to the file.  It <strong>will not</strong> be
     * interpreted as text.
     *
     * @param stream stream to read data from.
     * @throws IOException if an IOException occurs.
     */
    public void append(InputStream stream) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, stream);
    }

    /**
     * Append the text at the end of the File without writing a BOM,
     * using a specified encoding.
     *
     * @param text    the text to append at the end of the File
     * @param charset the charset used
     * @throws IOException if an IOException occurs.
     */
    public void append(Object text, String charset) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, text, charset);
    }

    /**
     * Append the text supplied by the Writer at the end of the File
     * without writing a BOM, using a specified encoding.
     *
     * @param writer  the Writer supplying the text to append at the end of the File
     * @param charset the charset used
     * @throws IOException if an IOException occurs.
     */
    public void append(Writer writer, String charset) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, writer, charset);
    }

    /**
     * Append the text supplied by the Reader at the end of the File
     * without writing a BOM, using a specified encoding.
     *
     * @param reader  the Reader supplying the text to append at the end of the File
     * @param charset the charset used
     * @throws IOException if an IOException occurs.
     */
    public void append(Reader reader, String charset) throws IOException {
        checkAccessible();
        ResourceGroovyMethods.append(this.internal, reader, charset);
    }

    /**
     * Invokes the closure for each 'child' file in this 'parent' folder/directory.
     * Both regular files and subfolders/subdirectories can be processed depending
     * on the fileType enum value.
     *
     * @param fileType if normal files or directories or both should be processed
     * @param closure  the closure to invoke
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     */
    public void eachFile(final FileType fileType, @ClosureParams(value = SimpleType.class, options = "java.io.File") final Closure<?> closure)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachFile(this.internal, fileType, wrapped);
    }

    /**
     * Invokes the closure for each 'child' file in this 'parent' folder/directory.
     * Both regular files and subfolders/subdirectories are processed.
     *
     * @param closure a closure (the parameter passed is the 'child' file)
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see java.io.File#listFiles()
     * @see ResourceGroovyMethods#eachFile(java.io.File, groovy.io.FileType, groovy.lang.Closure)
     */
    public void eachFile(@ClosureParams(value = SimpleType.class, options = "java.io.File")
                         final Closure<?> closure) throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachFile(this.internal, wrapped);
    }

    /**
     * Invokes the closure for each subdirectory in this directory,
     * ignoring regular files.
     *
     * @param closure a closure (the parameter passed is the subdirectory file)
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see java.io.File#listFiles()
     * @see ResourceGroovyMethods#eachFile(java.io.File, groovy.io.FileType, groovy.lang.Closure)
     */
    public void eachDir(
            @ClosureParams(value = SimpleType.class, options = "java.io.File") Closure<?> closure) throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachDir(this.internal, wrapped);
    }

    /**
     * Processes each descendant file in this directory and any subdirectories.
     * Processing consists of potentially calling <code>closure</code> passing it the current
     * file (which may be a normal file or subdirectory) and then if a subdirectory was encountered,
     * recursively processing the subdirectory. Whether the closure is called is determined by whether
     * the file was a normal file or subdirectory and the value of fileType.
     *
     * @param fileType if normal files or directories or both should be processed
     * @param closure  the closure to invoke on each file
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     */
    public void eachFileRecurse(final FileType fileType, @ClosureParams(value = SimpleType.class, options = "java.io.File") final Closure<?> closure)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachFileRecurse(this.internal, fileType, wrapped);
    }

    /**
     * Processes each descendant file in this directory and any subdirectories.
     * Processing consists of potentially calling <code>closure</code> passing it the current
     * file (which may be a normal file or subdirectory) and then if a subdirectory was encountered,
     * recursively processing the subdirectory.
     * <p>
     * The traversal can be adapted by providing various options in the <code>options</code> Map according
     * to the following keys:<dl>
     * <dt>type</dt><dd>A {@link groovy.io.FileType} enum to determine if normal files or directories or both are processed</dd>
     * <dt>preDir</dt><dd>A {@link groovy.lang.Closure} run before each directory is processed and optionally returning a {@link groovy.io.FileVisitResult} value
     * which can be used to control subsequent processing.</dd>
     * <dt>preRoot</dt><dd>A boolean indicating that the 'preDir' closure should be applied at the root level</dd>
     * <dt>postDir</dt><dd>A {@link groovy.lang.Closure} run after each directory is processed and optionally returning a {@link groovy.io.FileVisitResult} value
     * which can be used to control subsequent processing. Particularly useful when strict depth-first traversal is required.</dd>
     * <dt>postRoot</dt><dd>A boolean indicating that the 'postDir' closure should be applied at the root level</dd>
     * <dt>visitRoot</dt><dd>A boolean indicating that the given closure should be applied for the root dir
     * (not applicable if the 'type' is set to {@link groovy.io.FileType#FILES})</dd>
     * <dt>maxDepth</dt><dd>The maximum number of directory levels when recursing
     * (default is -1 which means infinite, set to 0 for no recursion)</dd>
     * <dt>filter</dt><dd>A filter to perform on traversed files/directories (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method). If set,
     * only files/dirs which match are candidates for visiting.</dd>
     * <dt>nameFilter</dt><dd>A filter to perform on the name of traversed files/directories (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method). If set,
     * only files/dirs which match are candidates for visiting. (Must not be set if 'filter' is set)</dd>
     * <dt>excludeFilter</dt><dd>A filter to perform on traversed files/directories (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method).
     * If set, any candidates which match won't be visited.</dd>
     * <dt>excludeNameFilter</dt><dd>A filter to perform on the names of traversed files/directories (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method).
     * If set, any candidates which match won't be visited. (Must not be set if 'excludeFilter' is set)</dd>
     * <dt>sort</dt><dd>A {@link groovy.lang.Closure} which if set causes the files and subdirectories for each directory to be processed in sorted order.
     * Note that even when processing only files, the order of visited subdirectories will be affected by this parameter.</dd>
     * </dl>
     * This example prints out file counts and size aggregates for groovy source files within a directory tree:
     * <pre>
     * def totalSize = 0
     * def count = 0
     * def sortByTypeThenName = { a, b {@code ->}
     *     a.isFile() != b.isFile() ? a.isFile() {@code <=>} b.isFile() : a.name {@code <=>} b.name
     * }
     * rootDir.traverse(
     *         type         : FILES,
     *         nameFilter   : ~/.*\.groovy/,
     *         preDir       : { if (it.name == '.svn') return SKIP_SUBTREE },
     *         postDir      : { println "Found $count files in $it.name totalling $totalSize bytes"
     *                         totalSize = 0; count = 0 },
     *         postRoot     : true
     *         sort         : sortByTypeThenName
     * ) {it {@code ->} totalSize += it.size(); count++ }
     * </pre>
     *
     * @param options a Map of options to alter the traversal behavior
     * @param closure the Closure to invoke on each file/directory and optionally returning a {@link groovy.io.FileVisitResult} value
     *                which can be used to control subsequent processing
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory or illegal filter combinations are supplied
     * @see DefaultGroovyMethods#sort(Iterable, groovy.lang.Closure)
     * @see groovy.io.FileVisitResult
     * @see groovy.io.FileType
     */
    public void traverse(@NamedParam(value = "type", type = FileType.class)
                         @NamedParam(value = "preDir", type = Closure.class)
                         @NamedParam(value = "preRoot", type = Boolean.class)
                         @NamedParam(value = "postDir", type = Closure.class)
                         @NamedParam(value = "postRoot", type = Boolean.class)
                         @NamedParam(value = "visitRoot", type = Boolean.class)
                         @NamedParam(value = "maxDepth", type = Integer.class)
                         @NamedParam(value = "filter")
                         @NamedParam(value = "nameFilter")
                         @NamedParam(value = "excludeFilter")
                         @NamedParam(value = "excludeNameFilter")
                         @NamedParam(value = "sort", type = Closure.class) final Map<String, ?> options,
                         @ClosureParams(value = SimpleType.class, options = "java.io.File") final Closure<?> closure)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.traverse(this.internal, options, wrapped);
    }

    /**
     * Processes each descendant file in this directory and any subdirectories.
     * Convenience method for {@link ResourceGroovyMethods#traverse(java.io.File, java.util.Map, groovy.lang.Closure)} when
     * no options to alter the traversal behavior are required.
     *
     * @param closure the Closure to invoke on each file/directory and optionally returning a {@link groovy.io.FileVisitResult} value
     *                which can be used to control subsequent processing
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see ResourceGroovyMethods#traverse(java.io.File, java.util.Map, groovy.lang.Closure)
     */
    public void traverse(@ClosureParams(value = SimpleType.class, options = "java.io.File") final Closure<?> closure)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.traverse(this.internal, wrapped);
    }

    /**
     * Invokes the closure specified with key 'visit' in the options Map
     * for each descendant file in this directory tree. Convenience method
     * for {@link ResourceGroovyMethods#traverse(java.io.File, java.util.Map, groovy.lang.Closure)} allowing the 'visit' closure
     * to be included in the options Map rather than as a parameter.
     *
     * @param options a Map of options to alter the traversal behavior
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory or illegal filter combinations are supplied
     * @see ResourceGroovyMethods#traverse(java.io.File, java.util.Map, groovy.lang.Closure)
     */
    public void traverse(@NamedParam(value = "type", type = FileType.class)
                         @NamedParam(value = "preDir", type = Closure.class)
                         @NamedParam(value = "preRoot", type = Boolean.class)
                         @NamedParam(value = "postDir", type = Closure.class)
                         @NamedParam(value = "postRoot", type = Boolean.class)
                         @NamedParam(value = "visitRoot", type = Boolean.class)
                         @NamedParam(value = "maxDepth", type = Integer.class)
                         @NamedParam(value = "filter")
                         @NamedParam(value = "nameFilter")
                         @NamedParam(value = "excludeFilter")
                         @NamedParam(value = "excludeNameFilter")
                         @NamedParam(value = "sort", type = Closure.class) final Map<String, ?> options)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        ResourceGroovyMethods.traverse(this.internal, options);
    }

    /**
     * Processes each descendant file in this directory and any subdirectories.
     * Processing consists of calling <code>closure</code> passing it the current
     * file (which may be a normal file or subdirectory) and then if a subdirectory was encountered,
     * recursively processing the subdirectory.
     *
     * @param closure a Closure
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see ResourceGroovyMethods#eachFileRecurse(java.io.File, groovy.io.FileType, groovy.lang.Closure)
     */
    public void eachFileRecurse(
            @ClosureParams(value = SimpleType.class, options = "java.io.File") Closure<?> closure) throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachFileRecurse(this.internal, wrapped);
    }

    /**
     * Recursively processes each descendant subdirectory in this directory.
     * Processing consists of calling <code>closure</code> passing it the current
     * subdirectory and then recursively processing that subdirectory.
     * Regular files are ignored during traversal.
     *
     * @param closure a closure
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see ResourceGroovyMethods#eachFileRecurse(java.io.File, groovy.io.FileType, groovy.lang.Closure)
     */
    public void eachDirRecurse(
            @ClosureParams(value = SimpleType.class, options = "java.io.File")
            final Closure<?> closure) throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachDirRecurse(this.internal, wrapped);
    }

    /**
     * Invokes the closure for each file whose name (file.name) matches the given nameFilter in the given directory
     * - calling the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method to determine if a match occurs.  This method can be used
     * with different kinds of filters like regular expressions, classes, ranges etc.
     * Both regular files and subdirectories may be candidates for matching depending
     * on the value of fileType.
     * <pre>
     * // collect names of files in baseDir matching supplied regex pattern
     * import static groovy.io.FileType.*
     * def names = []
     * baseDir.eachFileMatch FILES, ~/foo\d\.txt/, { names {@code <<} it.name }
     * assert names == ['foo1.txt', 'foo2.txt']
     *
     * // remove all *.bak files in baseDir
     * baseDir.eachFileMatch FILES, ~/.*\.bak/, { File bak {@code ->} bak.delete() }
     *
     * // print out files &gt; 4K in size from baseDir
     * baseDir.eachFileMatch FILES, { new File(baseDir, it).size() {@code >} 4096 }, { println "$it.name ${it.size()}" }
     * </pre>
     *
     * @param fileType   whether normal files or directories or both should be processed
     * @param nameFilter the filter to perform on the name of the file/directory (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method)
     * @param closure    the closure to invoke
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     */
    public void eachFileMatch(final FileType fileType, final Object nameFilter,
                              @ClosureParams(value = SimpleType.class, options = "java.io.File") final Closure<?> closure)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachFileMatch(this.internal, fileType, nameFilter, wrapped);
    }

    /**
     * Invokes the closure for each file whose name (file.name) matches the given nameFilter in the given directory
     * - calling the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method to determine if a match occurs.  This method can be used
     * with different kinds of filters like regular expressions, classes, ranges etc.
     * Both regular files and subdirectories are matched.
     *
     * @param nameFilter the nameFilter to perform on the name of the file (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method)
     * @param closure    the closure to invoke
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see ResourceGroovyMethods#eachFileMatch(java.io.File, groovy.io.FileType, java.lang.Object, groovy.lang.Closure)
     */
    public void eachFileMatch(final Object nameFilter, @ClosureParams(value = SimpleType.class, options = "java.io.File") final Closure<?> closure)
            throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachFileMatch(this.internal, nameFilter, wrapped);
    }

    /**
     * Invokes the closure for each subdirectory whose name (dir.name) matches the given nameFilter in the given directory
     * - calling the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method to determine if a match occurs.  This method can be used
     * with different kinds of filters like regular expressions, classes, ranges etc.
     * Only subdirectories are matched; regular files are ignored.
     *
     * @param nameFilter the nameFilter to perform on the name of the directory (using the {@link DefaultGroovyMethods#isCase(java.lang.Object, java.lang.Object)} method)
     * @param closure    the closure to invoke
     * @throws FileNotFoundException    if the given directory does not exist
     * @throws IllegalArgumentException if the provided File object does not represent a directory
     * @see ResourceGroovyMethods#eachFileMatch(java.io.File, groovy.io.FileType, java.lang.Object, groovy.lang.Closure)
     */
    public void eachDirMatch(final Object nameFilter, @ClosureParams(value = SimpleType.class, options = "java.io.File")
    final Closure<?> closure) throws FileNotFoundException, IllegalArgumentException {
        checkAccessible();
        Closure<?> wrapped = new LambdaClosure<>(closure.getOwner(), closure.getThisObject(),
                                                 args -> closure.call(new GroovyFile((File) args[0])));
        ResourceGroovyMethods.eachDirMatch(this.internal, nameFilter, wrapped);
    }

    /**
     * Deletes a directory with all contained files and subdirectories.
     * <p>The method returns
     * <ul>
     * <li>true, when deletion was successful</li>
     * <li>true, when it is called for a non existing directory</li>
     * <li>false, when it is called for a file which isn't a directory</li>
     * <li>false, when directory couldn't be deleted</li>
     * </ul>
     *
     * @return true if the file doesn't exist or deletion was successful
     */
    public boolean deleteDir() {
        checkAccessible();
        return ResourceGroovyMethods.deleteDir(this.internal);
    }
}
