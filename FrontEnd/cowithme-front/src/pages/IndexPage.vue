<template>
	<div>
		<div v-if="info2.role == '강사'">
			<div class="q-pa-md" style="font-family: 'Elice Digital Baeum'">
				<AtomPlusButton @click="makeClass" flat></AtomPlusButton>
				<div class="q-gutter-md q-ml-sm">
					<template v-if="classes.length != 0 && !isLoading">
						<div
							class="flex column justify-between box q-pa-lg hvr-grow"
							v-for="clas in classes"
							:key="clas.classId"
						>
							<p style="font-size: 40px; font-family: 'MICEGothic Bold'">
								{{ clas.className }}
							</p>

							<div class="class-description">
								<p style="color: white; margin: 18px">
									{{ clas.classDescription }}
								</p>
							</div>
							<router-link
								:to="{
									name: 'classDetail',
									params: {
										classId: clas.classId,
									},
								}"
								style="text-decoration: none; color: inherit"
								><q-btn
									@click="
										goSetClassInfo(
											clas,
											clas.classId,
											clas.className,
											info2.userId,
										)
									"
									style="background: #00adb5; color: white; font-size: 16px"
									push
									>반 관리하기</q-btn
								>
							</router-link>
						</div>
					</template>
					<div
						v-else-if="classes.length == 0 && !isLoading"
						class="flex column justify-center items-center box q-pa-lg hvr-grow"
					>
						<p style="font-size: 24px; font-family: 'MICEGothic Bold'">
							위의 버튼을 눌러 반을 생성 해주세요!
						</p>
					</div>
				</div>
			</div>
		</div>
		<div v-if="info2.role == '학생'" style="height: 100vh">
			<div
				class="q-pa-xl col flex justify-center"
				style="font-family: 'Elice Digital Baeum'"
			>
				<div class="q-gutter-md row">
					<div class="class-info hvr-grow relative-position">
						<div v-if="isInClass && !isLoading">
							<q-btn
								class="absolute-top-right q-ma-md"
								round
								icon="refresh"
								size="13px"
								flat
								:loading="loading"
								@click="refreshActiveLecture"
							/>
							<div class="col q-ma-lg q-pt-lg">
								<!-- 반, 강의제목, 날짜 -->
								<div class="info" style="margin-bottom: 20px">
									<p style="font-size: 40px; font-weight: bold">
										<span class="highlight-container"
											><span class="highlight">
												{{ testTest.className }}
											</span></span
										>의 강의
									</p>
									<p style="font-size: 18px">강사: {{ testTest.tutorName }}</p>
								</div>
							</div>
							<div class="col">
								<AtomBasic1Button
									v-if="isActiveLecture"
									class="button"
									push
									label="강의 입장"
									style="font-family: 'Elice Digital Baeum'"
									@click="enterLecture"
								></AtomBasic1Button>
							</div>
						</div>
						<div
							v-else-if="!isInClass && !isLoading"
							class="row justify-center items-center"
							style="height: 100%"
						>
							<h5>강사님을 통해 반 등록을 먼저 진행해주세요!</h5>
						</div>
					</div>

					<div class="row">
						<q-date
							class="hvr-grow test"
							v-model="newCurrentDate"
							:events="events"
							event-color="orange"
							color="teal"
							style="font-family: 'Elice Digital Baeum'"
							landscape
						/>
					</div>
				</div>
			</div>
			<div></div>
		</div>
	</div>
</template>

<script>
import { api } from 'src/boot/axios.js';
import { onMounted, ref } from 'vue';
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';
import { date } from 'quasar';

// import CalendarInfo from 'src/components/organisms/home/CalendarInfo.vue';
// import ClassInfo from 'src/components/organisms/home/ClassInfo.vue';
// import LectureTimeHistory from 'src/components/organisms/home/LectureTimeHistory.vue';
import AtomPlusButton from 'src/components/atoms/AtomPlusButton.vue';
import AtomBasic1Button from 'src/components/atoms/AtomBasic1Button.vue';
import { useClassStore } from 'src/stores';
import { commonVideoData } from 'src/stores/Video/common.js';
import { studentVideoData } from 'src/stores/Video/student.js';

export default defineComponent({
	name: 'IndexPage',
	components: {
		// CalendarInfo,
		// ClassInfo,
		// LectureTimeHistory,
		AtomPlusButton,
		AtomBasic1Button,
	},
	setup() {
		const classes = ref([]);
		const testTest = ref([]);
		const HOST = 'https://i7a304.p.ssafy.io/api/v1';
		const baseUrl = `${HOST}`;
		const piniaCommonVideoData = commonVideoData();
		const piniaStudentVideoData = studentVideoData();
		const router = useRouter();
		const events = ref([]);

		var user2 = null;
		var info2 = null;
		const user = localStorage.getItem('user');
		const info = localStorage.getItem('info');
		if (typeof user !== 'undefined') {
			user2 = JSON.parse(user);
		}
		if (typeof info !== 'undefined') {
			info2 = JSON.parse(info);
		}

		const userId = info2.userId;
		console.log(userId);
		const isLoading = ref(true);
		if (info2.role == '강사') {
			// 강사일 경우 반 정보를 불러옴
			isLoading.value = true;
			api
				.get(`${baseUrl}/tutor/${userId}/classes`)
				.then(res => {
					console.log(res);
					console.log(res.data);
					classes.value = res.data.classes;
					localStorage.setItem('classes', JSON.stringify(res.data.classes));
					isLoading.value = false;
				})
				.catch(err => {
					console.log(err);
					isLoading.value = false;
				});
			// classes.value.push(...temp.classes);
			// console.log(temp.classes);
			// console.log(classes.value);
			// console.log(classes.value[0]);
		}

		const isInClass = ref(false);
		onMounted(async () => {
			if (info2.role == '학생') {
				isLoading.value = true;
				try {
					const res = await api.get(`/users/${userId}/class`);
					testTest.value = res.data.result;
					console.log(res.data);
					localStorage.setItem('testTest', JSON.stringify(res.data));
					isInClass.value = true;
					isLoading.value = false;
					getActiveLecture();
				} catch (error) {
					console.log('학생인데 반 없음', error);
					isLoading.value = false;
				}
			}
		});

		async function makeClass() {
			await router.push({ path: '/makeClass' });
		}
		const classStore = useClassStore();

		async function goSetClassInfo(classInfo, classId, className, userId) {
			classStore.setClassInfo(classInfo, classId, className, userId);
		}

		console.log('@@@@', localStorage);

		async function enterLecture() {
			if (!activeLecture.value) {
				console.log('활성화된 강의 없음');
				return;
			}

			piniaCommonVideoData.userInfo.userId = JSON.parse(
				localStorage.getItem('user'),
			).id;
			piniaCommonVideoData.userInfo.userKey = JSON.parse(
				localStorage.getItem('info'),
			).userId;
			piniaCommonVideoData.userInfo.userName = JSON.parse(
				localStorage.getItem('info'),
			).name;
			piniaCommonVideoData.userInfo.classKey = JSON.parse(
				localStorage.getItem('testTest'),
			).result.classId;

			piniaCommonVideoData.userInfo.className = testTest.value.className;
			piniaCommonVideoData.userInfo.conferenceName =
				activeLecture.value.conferenceName;
			await router.push({ path: '/loading' });
			await piniaCommonVideoData.getConferenceKey();
			await piniaStudentVideoData.getStudentList();

			try {
				const response = await api.post(`/records/attendances`, {
					conferenceId: piniaCommonVideoData.userInfo.conferenceKey,
					userId: piniaCommonVideoData.userInfo.userKey,
				});
				console.log('출입기록 생성 성공', response.data);
			} catch (error) {
				console.log('출입기록 생성 에러', error);
			}
			setTimeout(() => {
				router.push('/studentlecture');
			}, 1500);
			// router.push('/studentlecture');
		}

		const activeLecture = ref({});
		const isActiveLecture = ref(false);
		const getActiveLecture = async () => {
			try {
				let classId = JSON.parse(localStorage.getItem('testTest')).result
					.classId;
				const response = await api.get(`/conferences/${classId}/active`);
				console.log(response.data);
				activeLecture.value = response.data.conference;
				isActiveLecture.value = true;
			} catch (error) {
				console.log('active 강의 불러오기 에러(active 강의 없음)', error);
				isActiveLecture.value = false;
			}
		};

		// 새로고침
		const loading = ref(false);
		async function refreshActiveLecture() {
			// we set loading state
			loading.value = true;
			try {
				let classId = JSON.parse(localStorage.getItem('testTest')).result
					.classId;
				const response = await api.get(`/conferences/${classId}/active`);
				console.log(response.data);
				activeLecture.value = response.data.conference;
				// simulate a delay
				setTimeout(() => {
					// we're done, we reset loading state
					loading.value = false;
					isActiveLecture.value = true;
				}, 1000);
			} catch (error) {
				console.log('active 강의 불러오기 에러(active 강의 없음)', error);
				// simulate a delay
				setTimeout(() => {
					// we're done, we reset loading state
					loading.value = false;
					isActiveLecture.value = false;
				}, 1000);
			}
		}

		// 달력 관련
		const currentDate = Date.now();
		const formattedTime = date.formatDate(currentDate, 'YYYY/MM/DD');
		const newCurrentDate = ref(formattedTime);

		// get current year and month
		onMounted(async () => {
			var now = new Date();
			const currentYear = now.getFullYear();
			const currentMonth = now.getMonth();
			api
				.get(`records/attendances/${userId}/${currentYear}/${currentMonth + 1}`)
				.then(res => {
					console.log(res.data.attInfos);
					var attendances = res.data.attInfos;
					for (var i = 0; i < attendances.length; i++) {
						var attendance = attendances[i];
						var date = new Date(attendance.date);
						var formattedDate =
							date.getFullYear() +
							'/' +
							('0' + (date.getMonth() + 1)).slice(-2) +
							'/' +
							('0' + date.getDate()).slice(-2);
						attendance.date = formattedDate;
						if (attendance.attendance == 'Yes') {
							events.value.push(attendance.date);
							console.log(events.value);
						}
					}
					// setAttendance(response.data);
				});
		});

		return {
			loading,
			refreshActiveLecture,
			isInClass,
			activeLecture,
			isActiveLecture,
			isLoading,

			classes,
			testTest,
			HOST,
			baseUrl,
			piniaCommonVideoData,
			piniaStudentVideoData,
			router,
			user2,
			info2,

			makeClass,
			classStore,
			goSetClassInfo,
			enterLecture,
			getActiveLecture,
			newCurrentDate,

			splitterModel: ref(30),
			events,
			// currentYear,
			// currentMonth,
		};
	},
});
</script>

<style scoped>
@import url('src/css/app.css');

@font-face {
	font-family: 'MICEGothic Bold';
	src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2206-01@1.0/MICEGothic Bold.woff2')
		format('woff2');
	font-weight: 700;
	font-style: normal;
}
.class-info {
	height: 300px !important;
	min-width: 500px !important;
	background-color: white !important;
	border-radius: 10px !important;
	box-shadow: 3px 3px 3px 0px rgba(0, 0, 0, 0.2) !important;
}

.button {
	margin-top: 60px !important;
	margin-left: 20px !important;
}
.box {
	height: 300px !important;
	width: 50% !important;
	min-width: 500px !important;
	background-color: white !important;
	border-radius: 10px !important;
	box-shadow: 3px 3px 3px 2px rgba(0, 0, 0, 0.2) !important;
}
.class-description {
	background-color: #ff8d29 !important;
	width: 60% !important;
	height: 40% !important;
	border-radius: 10px !important;
	box-shadow: 1px 1px 3px 1px #dadce0 !important;
}
[v-cloak] {
	display: none !important;
}
.test {
	-webkit-filter: drop-shadow(0px 1px 1px rgba(0, 0, 0, 0.7)) !important;
	filter: drop-shadow(0px 1px 1px rgba(0, 0, 0, 0.7)) !important;
}
.highlight-container,
.highlight {
	position: relative;
}

.highlight-container {
	display: inline-block;
}
.highlight-container:before {
	content: ' ';
	display: block;
	height: 90%;
	width: 100%;
	margin-left: -3px;
	margin-right: -3px;
	position: absolute;
	background: #ffd500;
	transform: rotate(2deg);
	top: -1px;
	left: -1px;
	border-radius: 20% 25% 20% 24%;
	padding: 10px 3px 3px 10px;
}
</style>
