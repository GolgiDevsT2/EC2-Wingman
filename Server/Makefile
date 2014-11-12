#
# This Software (the “Software”) is supplied to you by Openmind Networks
# Limited ("Openmind") your use, installation, modification or
# redistribution of this Software constitutes acceptance of this disclaimer.
# If you do not agree with the terms of this disclaimer, please do not use,
# install, modify or redistribute this Software.
#
# TO THE MAXIMUM EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED ON AN
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER
# EXPRESS OR IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR
# CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
# PARTICULAR PURPOSE.
#
# Each user of the Software is solely responsible for determining the
# appropriateness of using and distributing the Software and assumes all
# risks associated with use of the Software, including but not limited to
# the risks and costs of Software errors, compliance with applicable laws,
# damage to or loss of data, programs or equipment, and unavailability or
# interruption of operations.
#
# TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW OPENMIND SHALL NOT
# HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, WITHOUT LIMITATION,
# LOST PROFITS, LOSS OF BUSINESS, LOSS OF USE, OR LOSS OF DATA), HOWSOEVER 
# CAUSED UNDER ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
# LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
# WAY OUT OF THE USE OR DISTRIBUTION OF THE SOFTWARE, EVEN IF ADVISED OF
# THE POSSIBILITY OF SUCH DAMAGES.
#

PKG_DIR		=	$(HOME)/Golgi-Pkg/LATEST/
JDK_BIN		=	/usr/bin
JRE_BIN		=	/usr/bin

JAVAC		=	$(JDK_BIN)/javac
JAVA		=	$(JRE_BIN)/java
CLASSPATH	=	$(PKG_DIR)/common/garrick_combined.jar
CLASSPATH	=	../../wa-16/golgi/garrick/FAKE_RELEASE_AREA/java/garrick_combined.jar

all:
	$(JAVA) -classpath $(CLASSPATH) com.openmindnetworks.golgi.garrick.Garrick -i ../Wingman.thrift  -jsf WingmanSvc.js
	cat $(PKG_DIR)/javascript/GolgiForNode.js WingmanSvc.js code.js | sed -e "s/__PUT_DEVKEY_HERE__/`cat ../Golgi.DevKey`/g" -e "s/__PUT_APPKEY_HERE__/`cat ../Golgi.AppKey`/g"  >runtime.js
	

clean:
	@rm -f runtime.js WingmanSvc.js

spotless:clean


